<img src="https://raw.githubusercontent.com/Fashionism-Bangkit-Capstone/Fashionism-Mobile-Development/Fashionism-UMKM-App/app/src/main/ic_launcher_new-playstore.png" width="100" height="100" align="right" />

# Fashionism UMKM App
Product based capstone project Bangkit 2023

This application is designed to UMKM owner to upload their product so the product is visible in user application

---

## Features

- **Sign In and Sign Up** 
- **Dashboard** 
- **Transaction statistic (Coming soon)**
- **Product CRUD** 
- **Product filter**

---

## Getting Started

To get started with the Fashionism UMKM App, follow these steps:

1. Click sign up text to create your account
2. If you already have an account, you can proceed to sign in
3. In the dashboard menu, there are statistic feature but not yet implemented but number of product and recent added product is work
4. In the all product menu, you can see your product list, filter your product, and add product
5. In the add product menu, you can upload your image using camera or file manager
6. In the detail of product, you can update and delete your product
7. In the profile menu, there are other menu that is update profile, change password, and help center (not yet implemented)
8. In the update profile menu, you can change your shop identity and upload your shop icon
9. In the change password menu, you can change your password. Make sure to input your older password and match the new password with the confirm new password

---

## Screenshots
<img src="https://raw.githubusercontent.com/Fashionism-Bangkit-Capstone/Fashionism-Mobile-Development/Fashionism-UMKM-App/readmeImg/app_screenshot.png" align="center" />

---

## Libraries We Use

| Library name  | Usages        | Dependency    |
| ------------- | ------------- | ------------- |
| [Retrofit2](https://square.github.io/retrofit/) | Request API and convert json response into an object | implementation "com.squareup.retrofit2:retrofit:2.9.0" <br> implementation "com.squareup.retrofit2:converter-gson:2.9.0" |
| [OkHttp](https://square.github.io/okhttp/) | Make a data request to the server | implementation "com.squareup.okhttp3:logging-interceptor:4.9.0" |
| [Lifecycle](https://developer.android.com/jetpack/androidx/releases/lifecycle?hl=id) | Connecting frontend and backend | implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1" <br> implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.6.1" <br> implementation 'androidx.activity:activity-ktx:1.7.0' |
| [Navigation component](https://developer.android.com/guide/navigation)| Navigation between pages | implementation "androidx.navigation:navigation-fragment-ktx:2.5.3" <br> implementation "androidx.navigation:navigation-ui-ktx:2.5.3" |
| [Circle imageview](https://github.com/hdodenhof/CircleImageView)| Custom circle imageview | implementation 'de.hdodenhof:circleimageview:3.1.0' |
| [Glide](https://github.com/bumptech/glide)| Media management and image loading  | implementation 'com.github.bumptech.glide:glide:4.15.1' |
| [Swipe refreshlayout](https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout?hl=id)| Refresh when swiping down  | implementation 'androidx.swiperefreshlayout:swiperefreshlayout:1.1.0' |
| [Chart](https://github.com/PhilJay/MPAndroidChart)| Custom chart  | implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0' |
| [Smooth bottombar](https://github.com/ibrahimsn98/SmoothBottomBar)| Custom bottombar  | implementation 'com.github.ibrahimsn98:SmoothBottomBar:1.7.9' |
| [CameraX](https://developer.android.com/training/camerax?hl=id) | Custom camera | implementation "androidx.camera:camera-camera2:1.2.2" <br> implementation "androidx.camera:camera-lifecycle:1.2.2" <br> implementation "androidx.camera:camera-view:1.2.2" |