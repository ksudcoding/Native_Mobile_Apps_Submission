

App Overview

Name: Kyle Sutherland
Student ID: 2506252

The application’s name is Study Timer 101, the must requirements that my app has are:

“The app has a single screen with a simple and clean user interface.” - The app had a simple layout with buttons that are explained clearly, all buttons are labelled with the correct information.

“The UI includes at least one widget presenting information to the user, and one interactive widget, such as a button or an input text field.” - The main screen of the app has a number input field where you can put a number from 1 to 60 mins, this is your study time.

“The app responds to user interactions. For example, clicking a button should trigger a simple action like displaying a message or changing the text on the screen.” - When clicking the total study time button, it pops up with an AlertDialog message, which displays infomation.

“The app maintains its state during configuration changes (e.g., screen rotations).” - Yes, when the screen is flipped the buttons and ui move in the correct places and the page stays on the correct page.

“One of the screens includes a list of items using, for example, a LazyColumn.” - The List of images in the background are in a lazy column which scrolls slowing in the background.

“The list should be populated with data loaded from the internet or any other source.” - 
The List of images are from https://www.pexels.com and https://www.vecteezy.com, both websites which offer copyright free images for use.


“The app should include use of ViewModels, UI State, and StateFlow in accordance with Android Architecture Components to enhance maintainability and scalability”
- The MainActivity, StudyUiState, StudyViewModel and QuoteApiService are all used to keep the code in line within accordance with android Architecture components to enhance maintainability scalability.

“The app includes at least two screens, with navigation between them. This could be achieved using Android’s Navigation Component or explicit intents.” - The app have 2 navigation screens, TimerScreen and TimeInputScreen. both can be accessed with buttons on both screens, The timerscreen has one in the bottom corner and TimeInputScreen has the start timer button widget to change pages if the conditions are met.

“Each screen includes a mixture of informative and interactive widgets.” - Both screens have text and interactive widgets.

“The app sends or loads data from the internet, using that data within the apps core functionality.” - The App is using https://dummyjson.com/quotes/ to grab the quotes and display them above the number input. 

“The app gracefully handles errors, such as failed network requests or empty data, and provides feedback to the user.” - If there is no internet then their will be backup quotes that will be displayed. If the app has in input that is empty or exceeds the limit of 60 inside the input.


8 UI/UX Design & The View Layer


Orientation Wireframes: 
The app has two screens, TimeInputScreen and TimerScreen both work with scaling and also keeps the elements inline when the screen is flipped. The scrollable containers fit the compact viewports cleanly.


<img width="452.5" height="969.5" alt="Group 22" src="https://github.com/user-attachments/assets/ebc6b6c0-0729-47ca-83e5-f1eb2d1a09b4" />
<img width="432" height="969.5" alt="Group 21" src="https://github.com/user-attachments/assets/6f419d6b-140f-471e-8517-81a193d703e2" />
<img width="969.5" height="432" alt="Group 25" src="https://github.com/user-attachments/assets/91a2d64f-90fb-482a-ab6e-88389242d161" />
<img width="432" height="969.5" alt="Group 24" src="https://github.com/user-attachments/assets/86aacdad-af4f-4097-ba8b-5f176371c699" />
<img width="969.5" height="432" alt="Group 23" src="https://github.com/user-attachments/assets/39848df4-25ea-4cfc-b37d-6c3e3a259ad6" />

Compose UI Structure: 
TimeInputScreen uses a vertical Column, a card displays the live API quotes, an OutlinedTextField for the minute input and an AlertDialog for the study time stats.

TimerScreen uses a central Box with a custom Canvas with a drawArc for the visual countdown ring. Text and IconButton elements for audio toggles and exit back to TimeInputScreen button.

State Hoisting: 
All logic are removed from the screens and put inside the StudyViewModel using StateFlow<StudyUiState>. This keeps both TimeInput and TimerScreen stateless, as they only take in data from the UiState data. Also Data moves down from the ViewModel and then onto the screen so it can be displayed, and actions flow up from the buttons through function parameters so the viewmodel can do what happens next.

Responsive Design: 
So for the orientation detection it uses Jetpack Compose and reads the LocalConfiguration to switch between the layouts (portrait and landscape). When in portrait mode the UI is a single centred column, with everything such as the quote card, title, input field and the stacked buttons. When changed to landscape, it switches to two columns: the quote card moves to the left side of the screen, and the timer input and the Start and Stats buttons sit on the right side. 
The header padding also shrinks in landscape, and verticalScroll keeps everything reachable on short screens. Inside the TimerScreen it keeps the timer in the middle when in landscape. The state is held in a StudyViewModel, so the timer and input don’t reset when the screen rotates. The design follows Material Design by using Material 3 components (Card, OutlinedTextField, Button, AlertDialog), dp/sp units and consistent spacing.


MVVM Architecture & State Management


The ViewModel:
My viewmodel that I use in my app is StudyViewModel. The application uses it to manage the logic and also to make sure that the rotation of the screen stays as it should. When the timer is counting down and the screen flips, it keeps the timer at the correct second. The StudyViewModel makes sure that the quote stays the same between rotations as well. It also exposes the data using StateFlow (uiState).

Unidirectional Data Flow (UDF):
For the Unidirectional Data Flow (UDF), a core action is when the user enters their study minutes and clicks start. First, the View captures this user action and calls confirmAndStartTimer() in the StudyViewModel. Then the viewmodel applies the business logic by checking that the input is between 1 and 60 minutes, making sure all errors are handled if invalid. Finally the updated state goes back to the view via StateFlow, causing the UI to transition from the TimeInputScreen to the TimerScreen

The Model (Data Layer):
The application uses Local storage and remote data, the local data for example would be the users total study time, this time is locally stored. For remote data, the application uses a web service via QuoteApiService to grab live motivational quotes over the internet when the app starts up, or when returning to the setup screen.

Schemas:
The application uses its data across both local and remote web services. Rather than using a full Room SQLite database, the app relies on an Android SharedPreferences key-value schema using the key "total_studied_millis" to record the users total study time across sessions. The application communicates with QuoteApiService to retrieve a JSON payload containing "quote" and "author" string fields, which are parsed and mapped directly into the application's StudyUiState to display daily motivational quotes.


Navigation & Resilience


The app uses a single activity with two main screens: TimeInputScreen and TimerScreen. Instead of passing arguments around in routes, navigation is handled right in the UI based on uiState.isTimerRunning, while the StudyViewModel keeps track of all the shared data. For error handling, if something goes wrong when fetching quotes over the network with QuoteApiService, the viewmodel catches the exception using a try-catch block inside viewModelScope.launch. It then updates the errorMessage in the StudyUiState so the app doesn't crash and the user knows what happened.


Privacy and Security Review


Data Privacy & Permissions:
The app keeps things simple and follows the principle of least privilege by requesting zero dangerous system permissions no location, camera, or external file access. The only permission it asks for is basic internet access so it can grab daily quotes over the web using Retrofit. All network traffic uses HTTPS to keep data secure.


Storage Security:
For data at rest, the local study statistics saved on the device don't hold any personally identifiable information (PII) or sensitive user details. It only saves a basic counter for your total study time, so no private user data is ever exposed.

Compliance Assessment & Remediation:
The app aligns well with standard Android guidelines for minimal permissions and secure network traffic. However, there are a couple of small security gaps during testing, like using unencrypted local storage and basic range validation on user inputs. To fix this before deploying to production, standard SharedPreferences would be upgraded to EncryptedSharedPreferences to keep saved data fully locked down.
