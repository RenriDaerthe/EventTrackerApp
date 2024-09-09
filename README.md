MainActivity.java:

Handles the main functionality of the app, including:
User login and registration screens.
Event calendar display.
Floating action button (FAB) for adding events.
Integrates various views such as CalendarView, ListView, EditText, TextView, and Button.


User.java:

Represents the User model with attributes id, username, and password.
Includes getter and setter methods for managing user information.


Event.java:

Represents the Event model, which stores event-specific data such as event ID, title, date, time, and description.


DatabaseHelper.java:

Manages user data with SQLite database functions.
Creates the users table in the SQLite database to store user information (username, password).
Provides methods for adding new users and checking login credentials.
