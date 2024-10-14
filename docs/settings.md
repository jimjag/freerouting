## Freerouting Settings Documentation

Freerouting uses a flexible settings system that allows users to customize various aspects of the application's
behavior. These settings can be managed through a JSON configuration file, command-line arguments, or environment
variables.

### Settings File (JSON)

The primary way to configure Freerouting is by using a JSON settings file. This file contains key-value pairs that
define various settings. Below is a detailed explanation of the available settings:

#### **`profile` Section**

- **`id`**: A unique identifier for the user's profile. This is typically a UUID (Universally Unique Identifier).
- **`email`**: The user's email address (optional).

#### **`gui` Section**

- **`enabled`**: Enables or disables the graphical user interface (GUI). If set to `false`, Freerouting will run in
  headless mode.
- **`input_directory`**: Specifies the default directory for opening design files.
- **`dialog_confirmation_timeout`**: Sets the timeout in seconds for dialog confirmations.

#### **`router` Section**

- **`default_preferred_direction_trace_cost`**: Cost factor for routing traces in the preferred direction.
- **`default_undesired_direction_trace_cost`**: Cost factor for routing traces in undesired directions.
- **`max_passes`**: Maximum number of routing passes.
- **`fanout_max_passes`**: Maximum number of passes for fanout routing.
- **`max_threads`**: Maximum number of threads to use for routing.
- **`improvement_threshold`**: Minimum improvement required to continue routing.
- **`trace_pull_tight_accuracy`**: Accuracy for pulling traces tight.
- **`allowed_via_types`**: Enables or disables the use of different via types.
- **`via_costs`**: Cost factor for using vias.
- **`plane_via_costs`**: Cost factor for using vias on plane layers.
- **`start_ripup_costs`**: Cost factor for ripping up existing traces.
- **`automatic_neckdown`**: Enables or disables automatic neckdown of traces.

#### **`usage_and_diagnostic_data` Section**

- **`disable_analytics`**: Disables sending anonymous usage and diagnostic data.
- **`analytics_modulo`**: Sends usage data after every Nth run, where N is the value of `analytics_modulo`.

#### **`feature_flags` Section**

- **`logging`**: Enables or disables logging.
- **`multi_threading`**: Enables or disables multi-threaded routing.
- **`select_mode`**: Enables or disables select mode in the GUI.
- **`macros`**: Enables or disables macro functionality.
- **`other_menu`**: Enables or disables the "Other" menu in the GUI.
- **`snapshots`**: Enables or disables snapshot functionality.
- **`file_load_dialog_at_startup`**: Shows or hides the file load dialog at startup.
- **`save_jobs`**: Enables or disables saving routing jobs to disk.

#### **`api_server` Section**

- **`enabled`**: Enables or disables the built-in API server.
- **`http_allowed`**: Allows or disallows HTTP connections to the API server.
- **`endpoints`**: A list of endpoints that the API server will listen on. Each endpoint is specified as
  `[protocol]://[host]:[port]`.

#### **`version` Section**

- **`version`**: Specifies the version of the settings file.

### Command Line Arguments

Freerouting can also be configured using command-line arguments. These arguments override the settings specified in the
JSON configuration file.

**Example:**

java -jar freerouting.jar --gui.enabled=false --router.max_passes=200

### Environment Variables

Environment variables provide another way to override settings. The environment variable names correspond to the keys in
the JSON settings file, with periods replaced by underscores.

**Example:**

FREEROUTING__GUI__ENABLED=false
FREEROUTING__ROUTER__MAX_PASSES=200
java -jar freerouting.jar

## Settings Precedence

The settings are applied in the following order of precedence (highest to lowest):

1. **Command Line Arguments**
2. **Environment Variables**
3. **JSON Configuration File**
4. **Default Settings** (hardcoded in the application)
   This means that command-line arguments take precedence over environment variables, which in turn take precedence over
   the settings specified in the JSON configuration file. If a setting is not defined in any of these sources, the
   default value hardcoded in the application will be used.