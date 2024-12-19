# EndurAi
General description :
A sports companion application designed to support users in achieving their fitness goals
safely and effectively. Our goal is to provide a tool that not only helps beginners develop
good exercise habits but also serves as a valuable resource for athletes of all levels looking
to monitor and improve their performance. Overall, our app aims to prevent injuries by
promoting proper form and technique, set personalized workouts for continuous motivation, and
track progress in an interactive way.
App features :
- Using computer vision and machine learning, the app analyzes workout sessions in
real-time through the user’s device camera. It provides instant feedback to ensure
proper form and technique, such as correcting shoulder position during a plank but
also helps counting repetitions and keeping track of key performance metrics
(repetition for Push ups, squats etc and time for chair, planck etc).
- The app offers a running feature that allows to track their path and save it. By
using GPS data, the app also measures pace and distance in real-time to help users achieve
their target times and improve their endurance.
- Users can view their workout history in a calendar format. This feature allows users
to track their progress over time, visualize their consistency, and stay motivated to
reach their fitness goals.
- The app visualizes progress through graphs and charts, highlighting improvement in
areas such as strength or endurance.
- The app offers workout creation for both bodyweight and yoga. And then the user can
follow the flow of his workout. While doing the workout he can film himself and compare
his position with a coach video.
- When the user finishes a workout he can save it, so that he can import it back if he wants.
- 
Technical approach :
We use Google MediaPipe to capture 3D body joints positions and compute angles
for accurate form correction. For moving exercises (squats, push-ups…) , the app use
this data to track movement paths and provide feedback if the user happens to deviate from
the correct form. For static exercises (planks…), the app makes sure that all critical
angles are within safe ranges. The app can also count repetition for example for push ups.

External Resources :
1. Our app uses Google Firebase to store user data such as
workout history, progress, friends, or even preferences.
2. User Support: Users can create accounts using Android’s built-in Google
authentication to track their progress.
3. Sensor use: We use the camera for workout analysis as well as GPS for running
plans.
4. Offline mode: Without internet connectivity, users are still able to perform
cached workouts. They can also still use the ML feature.

# Figma 
We have a Figma project for the app design and navigation as well as the architecture diagram. Here is the [link](https://www.figma.com/design/rZgylXKE9PmQgKigHpzMtr/Sport-Companion?node-id=0-1&m=dev&t=RZbHJEZFt2Uhlm2P-1) to the project.
