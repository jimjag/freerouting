package app.freerouting.gui;

import app.freerouting.management.TextManager;
import app.freerouting.management.analytics.FRAnalytics;

import javax.swing.*;

/**
 * Popup menu used while constructing a cornered shape.
 */
class PopupMenuCornerItemConstruction extends JPopupMenu
{

  private final BoardPanel board_panel;

  /**
   * Creates a new instance of CornerItemConstructionPopupMenu
   */
  PopupMenuCornerItemConstruction(BoardFrame p_board_frame)
  {
    this.board_panel = p_board_frame.board_panel;

    TextManager tm = new TextManager(this.getClass(), p_board_frame.get_locale());

    JMenuItem popup_add_corner_menuitem = new JMenuItem();
    popup_add_corner_menuitem.setText(tm.getText("add_corner"));
    popup_add_corner_menuitem.addActionListener(
        // Same action as if the left button is clicked with
        // the current mouse coordinates in this situation
        // because the left button is a shortcut for this action.
        evt -> board_panel.board_handling.left_button_clicked(board_panel.right_button_click_location));
    popup_add_corner_menuitem.addActionListener(evt -> FRAnalytics.buttonClicked("popup_add_corner_menuitem", popup_add_corner_menuitem.getText()));

    this.add(popup_add_corner_menuitem);

    JMenuItem popup_close_menuitem = new JMenuItem();
    popup_close_menuitem.setText(tm.getText("close"));
    popup_close_menuitem.addActionListener(evt -> board_panel.board_handling.return_from_state());
    popup_close_menuitem.addActionListener(evt -> FRAnalytics.buttonClicked("popup_close_menuitem", popup_close_menuitem.getText()));

    this.add(popup_close_menuitem);

    JMenuItem popup_cancel_menuitem = new JMenuItem();
    popup_cancel_menuitem.setText(tm.getText("cancel"));
    popup_cancel_menuitem.addActionListener(evt -> board_panel.board_handling.cancel_state());
    popup_cancel_menuitem.addActionListener(evt -> FRAnalytics.buttonClicked("popup_cancel_menuitem", popup_cancel_menuitem.getText()));

    this.add(popup_cancel_menuitem);
  }
}