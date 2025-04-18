package app.freerouting.interactive;

import app.freerouting.board.*;
import app.freerouting.boardgraphics.GraphicsContext;
import app.freerouting.datastructures.UndoableObjects;
import app.freerouting.geometry.planar.FloatPoint;
import app.freerouting.management.TextManager;
import app.freerouting.rules.Net;

import java.awt.*;
import java.util.*;

/**
 * Creates all incompletes (ratsnest) to display them on the screen
 */
public class RatsNest
{

  public final int max_connections;
  private final NetIncompletes[] net_incompletes;
  private final boolean[] is_filtered;
  public boolean hidden = false;

  /**
   * Creates a new instance of RatsNest
   */
  public RatsNest(BasicBoard p_board)
  {
    int max_net_no = p_board.rules.nets.max_net_no();
    // Create the net item lists at once for performance reasons.
    Vector<Collection<Item>> net_item_lists = new Vector<>(max_net_no);
    for (int i = 0; i < max_net_no; ++i)
    {
      net_item_lists.add(new LinkedList<>());
    }
    Iterator<UndoableObjects.UndoableObjectNode> it = p_board.item_list.start_read_object();
    for (; ; )
    {
      Item curr_item = (Item) p_board.item_list.read_object(it);
      if (curr_item == null)
      {
        break;
      }
      if (curr_item instanceof Connectable)
      {
        for (int i = 0; i < curr_item.net_count(); ++i)
        {
          net_item_lists
              .get(curr_item.get_net_no(i) - 1)
              .add(curr_item);
        }
      }
    }
    this.max_connections = net_item_lists
        .stream()
        .mapToInt(Collection::size)
        .sum() - net_item_lists.size();
    this.net_incompletes = new NetIncompletes[max_net_no];
    this.is_filtered = new boolean[max_net_no];
    for (int i = 0; i < net_incompletes.length; ++i)
    {
      net_incompletes[i] = new NetIncompletes(i + 1, net_item_lists.get(i), p_board);
      is_filtered[i] = false;
    }
  }

  /**
   * Recalculates the incomplete connections for the input net
   */
  public void recalculate(int p_net_no, BasicBoard p_board)
  {
    if (p_net_no >= 1 && p_net_no <= net_incompletes.length)
    {
      Collection<Item> item_list = p_board.get_connectable_items(p_net_no);
      net_incompletes[p_net_no - 1] = new NetIncompletes(p_net_no, item_list, p_board);
    }
  }

  /**
   * Recalculates the incomplete connections for the input net with the input item list.
   */
  public void recalculate(int p_net_no, Collection<Item> p_item_list, BasicBoard p_board)
  {
    if (p_net_no >= 1 && p_net_no <= net_incompletes.length)
    {
      // copy p_item_list, because it will be changed inside the constructor of NetIncompletes
      Collection<Item> item_list = new LinkedList<>(p_item_list);
      net_incompletes[p_net_no - 1] = new NetIncompletes(p_net_no, item_list, p_board);
    }
  }

  /**
   * Returns the number of incomplete connections (airlines) of the ratsnest.
   * This values might be higher than the number of nets, if the nets have multiple unrouted connections.
   */
  public int incomplete_count()
  {
    int result = 0;
    for (int i = 0; i < net_incompletes.length; ++i)
    {
      result += net_incompletes[i].count();
    }
    return result;
  }

  public int incomplete_count(int p_net_no)
  {
    if (p_net_no <= 0 || p_net_no > net_incompletes.length)
    {
      return 0;
    }
    return net_incompletes[p_net_no - 1].count();
  }

  public int length_violation_count()
  {
    int result = 0;
    for (int i = 0; i < net_incompletes.length; ++i)
    {
      if (net_incompletes[i].get_length_violation() != 0)
      {
        ++result;
      }
    }
    return result;
  }

  /**
   * Returns the length of the violation of the length restriction of the net with number p_net_no,
   * {@literal >} 0, if the cumulative trace length is too big, {@literal <} 0, if the trace length
   * is too small, 0, if the trace length is ok or the net has no length restrictions
   */
  public double get_length_violation(int p_net_no)
  {
    if (p_net_no <= 0 || p_net_no > net_incompletes.length)
    {
      return 0;
    }
    return net_incompletes[p_net_no - 1].get_length_violation();
  }

  /**
   * Returns all airlines of the ratsnest.
   */
  public AirLine[] get_airlines()
  {
    AirLine[] result = new AirLine[incomplete_count()];
    int curr_index = 0;
    for (int i = 0; i < net_incompletes.length; ++i)
    {
      Collection<AirLine> curr_list = net_incompletes[i].incompletes;
      for (AirLine curr_line : curr_list)
      {
        result[curr_index] = curr_line;
        ++curr_index;
      }
    }
    return result;
  }

  public void hide()
  {
    hidden = true;
  }

  public void show()
  {
    hidden = false;
  }

  /**
   * Recalculate the length matching violations. Return false, if the length violations have not
   * changed.
   */
  public boolean recalculate_length_violations()
  {
    boolean result = false;
    for (int i = 0; i < net_incompletes.length; ++i)
    {
      if (net_incompletes[i].calc_length_violation())
      {
        result = true;
      }
    }
    return result;
  }

  /**
   * Used for example to hide the incompletes during interactive routing.
   */
  public boolean is_hidden()
  {
    return hidden;
  }

  /**
   * Sets the visibility filter for the incompletes of the input net.
   */
  public void set_filter(int p_net_no, boolean p_value)
  {
    if (p_net_no < 1 || p_net_no > is_filtered.length)
    {
      return;
    }
    is_filtered[p_net_no - 1] = p_value;
  }

  public void draw(Graphics p_graphics, GraphicsContext p_graphics_context)
  {
    boolean draw_length_violations_only = this.hidden;

    for (int i = 0; i < net_incompletes.length; ++i)
    {
      if (!is_filtered[i])
      {
        net_incompletes[i].draw(p_graphics, p_graphics_context, draw_length_violations_only);
      }
    }
  }

  /**
   * Describes a single incomplete connection of the ratsnest.
   */
  public static class AirLine implements Comparable<AirLine>, ObjectInfoPanel.Printable
  {
    public final Net net;
    public final Item from_item;
    public final FloatPoint from_corner;
    public final Item to_item;
    public final FloatPoint to_corner;

    AirLine(Net p_net, Item p_from_item, FloatPoint p_from_corner, Item p_to_item, FloatPoint p_to_corner)
    {
      net = p_net;
      from_item = p_from_item;
      from_corner = p_from_corner;
      to_item = p_to_item;
      to_corner = p_to_corner;
    }

    @Override
    public int compareTo(AirLine p_other)
    {
      return this.net.name.compareTo(p_other.net.name);
    }

    @Override
    public String toString()
    {
      return this.net.name + ": " + getItemInfo(from_item).text + " - " + getItemInfo(to_item).text;
    }

    private RatsNestItemInfo getItemInfo(Item p_item)
    {
      RatsNestItemInfo result = new RatsNestItemInfo();
      if (p_item instanceof Pin pin)
      {
        result.type = RatsNestItemType.PIN;
        result.componentName = pin.component_name();
        result.name = pin.name();
        result.text = pin.component_name() + ", " + pin.name();
      }
      else if (p_item instanceof Via via)
      {
        result.type = RatsNestItemType.VIA;
        result.componentName = via.component_name();
        result.text = "Via";
      }
      else if (p_item instanceof Trace trace)
      {
        result.type = RatsNestItemType.TRACE;
        result.componentName = trace.component_name();
        result.text = "Trace";
      }
      else if (p_item instanceof ConductionArea conductionArea)
      {
        result.type = RatsNestItemType.CONDUCTION_AREA;
        result.componentName = conductionArea.component_name();
        result.text = "Conduction Area";
      }
      else
      {
        result.type = RatsNestItemType.UNKNOWN;
        result.text = "Unknown";
      }
      return result;
    }

    @Override
    public void print_info(ObjectInfoPanel p_window, Locale p_locale)
    {
      TextManager tm = new TextManager(this.getClass(), p_locale);

      p_window.append_bold(tm.getText("incomplete"));
      p_window.append(" " + tm.getText("net") + " ");
      p_window.append(net.name);
      p_window.append(" " + tm.getText("from") + " ", "Incomplete Start Item", from_item);
      p_window.append(from_corner);
      p_window.append(" " + tm.getText("to") + " ", "Incomplete End Item", to_item);
      p_window.append(to_corner);
      p_window.newline();
    }
  }
}