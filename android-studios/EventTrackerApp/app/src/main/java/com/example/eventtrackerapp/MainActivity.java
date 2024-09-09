package com.example.eventtrackerapp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    private TextView switchToRegister, switchToLogin;
    private EditText emailInput, passwordInput, confirmPasswordInput;
    private CalendarView calendarView;
    private FloatingActionButton fab, fabHelp;
    private ListView eventsListView;
    private TextView eventsLabel;
    private FrameLayout container;
    private Databasehelper databaseHelper;
    private List<Event> eventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the main layout with the container

        databaseHelper = new Databasehelper(this); // Initialize the database helper

        container = findViewById(R.id.container); // Initialize the container

        // Initially load the login screen
        loadLoginScreen();
    }

    private void loadLoginScreen() {
        View loginScreen = getLayoutInflater().inflate(R.layout.login_screen, container, false);
        container.removeAllViews();
        container.addView(loginScreen);

        loginButton = loginScreen.findViewById(R.id.loginButton);
        switchToRegister = loginScreen.findViewById(R.id.switchToRegister);
        emailInput = loginScreen.findViewById(R.id.emailInput);
        passwordInput = loginScreen.findViewById(R.id.passwordInput);

        switchToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRegistrationScreen();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (databaseHelper.checkUser(username, password)) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                    loadHomeScreen();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadRegistrationScreen() {
        View registrationScreen = getLayoutInflater().inflate(R.layout.registration_screen, container, false);
        container.removeAllViews();
        container.addView(registrationScreen);

        registerButton = registrationScreen.findViewById(R.id.registerButton);
        switchToLogin = registrationScreen.findViewById(R.id.switchToLogin);
        emailInput = registrationScreen.findViewById(R.id.emailInput);
        passwordInput = registrationScreen.findViewById(R.id.passwordInput);
        confirmPasswordInput = registrationScreen.findViewById(R.id.confirmPasswordInput);

        switchToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLoginScreen();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();

                if (password.equals(confirmPassword)) {
                    if (databaseHelper.addUser(username, password)) {
                        Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        loadHomeScreen();
                    } else {
                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadHomeScreen() {
        View homeScreen = getLayoutInflater().inflate(R.layout.main_screen, container, false);
        container.removeAllViews();
        container.addView(homeScreen);

        calendarView = homeScreen.findViewById(R.id.calendarView);
        fab = homeScreen.findViewById(R.id.fab_add);
        fabHelp = homeScreen.findViewById(R.id.fab_help);
        eventsListView = homeScreen.findViewById(R.id.eventsListView);
        eventsLabel = homeScreen.findViewById(R.id.eventsLabel);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                displayEventsForDate(selectedDate);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadEventCreationEditScreen(null);
            }
        });

        fabHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettingsScreen();
            }
        });

        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event selectedEvent = (Event) parent.getItemAtPosition(position);
                loadEventCreationEditScreen(selectedEvent);
            }
        });
    }

    private void displayEventsForDate(String date) {
        List<Event> eventsForDate = new ArrayList<>();
        for (Event event : eventList) {
            if (event.getDate().equals(date)) {
                eventsForDate.add(event);
            }
        }
        ArrayAdapter<Event> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsForDate);
        eventsListView.setAdapter(adapter);
    }

    private void loadEventCreationEditScreen(final Event event) {
        View eventCreationEditScreen = getLayoutInflater().inflate(R.layout.event_creation_edit_screen, container, false);
        container.removeAllViews();
        container.addView(eventCreationEditScreen);

        EditText eventNameInput = eventCreationEditScreen.findViewById(R.id.eventNameInput);
        EditText eventDateInput = eventCreationEditScreen.findViewById(R.id.eventDateInput);
        EditText eventTimeInput = eventCreationEditScreen.findViewById(R.id.eventTimeInput);
        EditText eventDescriptionInput = eventCreationEditScreen.findViewById(R.id.eventDescriptionInput);
        Button saveEventButton = eventCreationEditScreen.findViewById(R.id.saveEventButton);
        Button cancelEventButton = eventCreationEditScreen.findViewById(R.id.cancelEventButton);

        if (event != null) {
            eventNameInput.setText(event.getName());
            eventDateInput.setText(event.getDate());
            eventTimeInput.setText(event.getTime());
            eventDescriptionInput.setText(event.getDescription());
        }

        eventDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                eventDateInput.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        eventTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                eventTimeInput.setText(String.format("%02d:%02d", hourOfDay, minute));
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        saveEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = eventNameInput.getText().toString();
                String eventDate = eventDateInput.getText().toString();
                String eventTime = eventTimeInput.getText().toString();
                String eventDescription = eventDescriptionInput.getText().toString();

                if (event == null) {
                    Event newEvent = new Event(eventName, eventDate, eventTime, eventDescription);
                    eventList.add(newEvent);
                } else {
                    event.setName(eventName);
                    event.setDate(eventDate);
                    event.setTime(eventTime);
                    event.setDescription(eventDescription);
                }

                Toast.makeText(MainActivity.this, "Event saved", Toast.LENGTH_SHORT).show();
                loadHomeScreen();
            }
        });

        cancelEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeScreen();
            }
        });
    }

    private void loadEventDetailsScreen() {
        View eventDetailsScreen = getLayoutInflater().inflate(R.layout.event_details_screen, container, false);
        container.removeAllViews();
        container.addView(eventDetailsScreen);

        // Initialize event details screen components and logic here
    }

    private void loadSettingsScreen() {
        View settingsScreen = getLayoutInflater().inflate(R.layout.setting_screen, container, false);
        container.removeAllViews();
        container.addView(settingsScreen);

        Button cancelSettingsButton = settingsScreen.findViewById(R.id.cancelSettingsButton);
        Button smsNotificationButton = settingsScreen.findViewById(R.id.smsNotificationButton);

        cancelSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHomeScreen();
            }
        });

        smsNotificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSmsNotificationScreen();
            }
        });

        // Initialize other settings screen components and logic here
    }

    private void loadSmsNotificationScreen() {
        View smsNotificationScreen = getLayoutInflater().inflate(R.layout.sms_notification_screen, container, false);
        container.removeAllViews();
        container.addView(smsNotificationScreen);

        final Switch smsNotificationsSwitch = smsNotificationScreen.findViewById(R.id.smsNotificationsSwitch);
        final EditText phoneNumberInput = smsNotificationScreen.findViewById(R.id.phoneNumberInput);
        Button saveSmsSettingsButton = smsNotificationScreen.findViewById(R.id.saveSmsSettingsButton);
        Button cancelSmsSettingsButton = smsNotificationScreen.findViewById(R.id.cancelSmsSettingsButton);

        smsNotificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            phoneNumberInput.setEnabled(isChecked);
        });

        saveSmsSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (smsNotificationsSwitch.isChecked()) {
                    String phoneNumber = phoneNumberInput.getText().toString();
                    if (!phoneNumber.isEmpty()) {
                        // Save phone number logic here
                        Toast.makeText(MainActivity.this, "Phone number saved: " + phoneNumber, Toast.LENGTH_SHORT).show();
                        loadSettingsScreen();
                    } else {
                        Toast.makeText(MainActivity.this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "SMS notifications are disabled", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelSmsSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSettingsScreen();
            }
        });
    }
}