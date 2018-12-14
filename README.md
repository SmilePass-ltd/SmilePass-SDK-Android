# SmilePass Android SDK

[ ![Latest Version](https://api.bintray.com/packages/smilepass-ltd/maven/com.smilepass.mobilesdk/images/download.svg) ](https://bintray.com/smilepass-ltd/maven/com.smilepass.mobilesdk/_latestVersion)

## Introduction
SmilePass is a face verification SDK for Android. This repo is the sample which demonstrate how to use SmilePass SDK in your app.
Using SmilePass, our clients can create unique and secure biometric profiles for each of their customers as they begin their journey together. Any future transactions or events that contain risk are verified against this profile.

**Our scalable security solution can be used to;**
* Provide frictionless and secure KYC for your customers
* Build trusted membership communities through identity management
* Reduce ticket, tout and payment fraud while increasing security
* Increase defences against fraudulent transactions and requests in a cost-effective way
* Streamline access to services without the need for hardware

SmilePass Android SDK is available for Android SDK greater than 14.

## Get Started

This guide is a quick start to add SmilePass biometric verification to an Android app. Android Studio is the recommended development environment for building an app with the SmilePass SDK.


## Prerequisites

### SmilePass API key
Your application needs an API key to access the features of SmilePass SDK. You can use it with any of your applications that use SmilePass Mobile SDKs and Cloud APIs. It supports an unlimited number of users.
To get API Key, [Contact SmilePass](https://smile-pass.com/contact).


## Add SmilePass to your app

### Step 1. Add SmilePass dependency
Add SmilePass Android SDK to your project. To do this, add the following dependency in your app level `build.gradle` file-
```gradle
implementation 'com.smilepass.mobilesdk:smilepass:{latest-version}'
```
The latest version can be found at the top of this file.
The library is indexed on Bintray JCenter repository with all versions [here](https://bintray.com/smilepass-ltd/maven/com.smilepass.mobilesdk).

### Step 2. Instantiate SmilePass client
Create an instance of SmilePass rest client and pass API Key as follows:
```java
SmilePassClient smilePassClient = new SmilePassRestClient("{{API_KEY}}");
```

### Step 3. Handle ClientException
You need to handle `ClientException` for creating an instance and calling any method of SmilePass-
```java
try {
    SmilePassClient smilePassClient = new SmilePassRestClient("{{API_KEY}}");
} catch (ClientException e) {
    e.printStackTrace();
    ClientError clientError = e.error;
}
```

### Step 4. Access feature methods
The methods of SmilePass can be accessed using the instance of `SmilePassClient`. For example-
```java
smilePassClient.register(registrationJsonObject, this);
```
where,
`registrationJsonObject` is the instance of `JSONObject` and the second argument is the callback listener

Make sure you have added `android.permission.INTERNET` permission in your application's AndroidManifest.xml file before calling any method from `SmilePassClient`.


### Step 5. Implement callback listener
Every feature method has last parameter as callback listener. For example, to use registration method, implement 
listener as follows:
```java
public class MainActivity extends AppCompatActivity implements OnRegistrationResponseListener {
...
}
```

and override callback method as follows:
```java
@Override
public void onRegistrationResponse(JSONObject jsonObject, Throwable throwable) {
    if (throwable != null) {
       if (throwable instanceof ServerException) {
          ServerError serverError = ((ServerException) throwable).error;
          Log.e(TAG, "onRegistrationResponse(): error=" + serverError.toString());
       } else {
          Log.e(TAG, "onRegistrationResponse(): error=" + throwable.getMessage());
       }
    } else {
          Log.i(TAG, "onRegistrationResponse(): json=" + jsonObject);
    }
}
```


You are all set to use cutting-edge face verification features of the SmilePass. 

## Documents
For the detailed information on how to register and verify a user using SmilePass, read our detailed documents-
* [SDK Setup](https://github.com/SmilePass-ltd/SmilePass-SDK-Android/wiki/SmilePass-SDK-Setup)
* [SmilePass Tutorials](https://github.com/SmilePass-ltd/SmilePass-SDK-Android/wiki/SmilePass-Tutorials)
* [Troubleshooting](https://github.com/SmilePass-ltd/SmilePass-SDK-Android/wiki/Troubleshooting)


## License
SmilePass Android SDK sample application is licensed with the MIT License. For more details, see [LICENSE](https://github.com/SmilePass-ltd/SmilePass-SDK-Android/blob/master/LICENSE).
