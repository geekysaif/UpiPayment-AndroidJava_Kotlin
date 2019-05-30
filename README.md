# Easy UPI Payment - Android Library 📱💳

![](Screenshots-Demo/GitHub-SocialPreview.png)

## Introduction
Hello Everyone, I have developed this Android library to ***easily implement UPI Payment Integration in Android app.*** <br>
UPI apps are *required to be installed already before using this library* because, internally this API calls UPI apps for payment processing. Before using it, make sure that your device is having atleast one UPI app installed. Otherwise it will unable to process the payments. <br>
This API is in beta, there are lot of improvements are still needed.

## Demo 
Start             |  Select UPI App             |  Complete Payment             |  Finish         
:-------------------------:|:-------------------------:|:-------------------------:|:-------------------------:
![](Screenshots-Demo/EasyUpiPay1.png)  |  ![](Screenshots-Demo/EasyUpiPay2.png)|  ![](Screenshots-Demo/EasyUpiPay3.png)|  ![](Screenshots-Demo/EasyUpiPay4.png)

## Implementation
You can clone this repository and just import this project in Android Studio. Sample app is in [`/app`](/app) directory.

### Gradle
In your `build.gradle` file of app module, add below dependency to import this library

```gradle
    dependencies {
      implementation 'com.shreyaspatil:EasyUpiPayment:0.1-beta'
    }
```

### Setting Up Activity
In Android app, make any activity where you want to implement payment integration. Here, I have created `MainActivity.java`

#### Initializing `EasyUpiPayment` :
You can see below code, these are minimum and mandatory calls to enable payment processing. If any of it is missed then error will generated.
```java
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(this)
                .setPayeeVpa("example@vpa")
                .setPayeeName("Example")
                .setDescription("Description or small note")
                .setAmount("10.00")
                .build();
```
**Calls and Descriptions :**
* `with()` : **Mandatory** and this call takes `Activity` as a parameter and.
* `setPayeeVpa()` : **Mandatory** and takes VPA address of payee for e.g. ***`shreyas@upi`***
* `setDescription()` : **Mandatory** and have to provide valid small note or description about payment. for e.g. *For Food*
* `setAmount()` : **Mandatory** and it takes amount in String decimal format (`xx.xx`) to be paid. *For e.g. 90.88* will pay Rs. 90.88.
* `setPayeeMerchantCode` : Payee Merchant code if present it should be passed.
* `setTransactionId()` : This field is used in Merchant Payments generated by PSPs.
* `setTransactionRefId()` : Transaction reference ID. This could be order number, subscription number, Bill ID, booking ID, insurance renewal reference, etc. Needed for merchant transactions and dynamic URL generation.
* `build()` : It will build and returns the `EasyUpiPayment` instance.

#### Proceed to Payment
To start the payment, just call `startPayment()` method of EasyUpiPayment and after that transaction is started.
```java
      easyUpiPayment.startPayment();
```

#### Event Callback Listeners
To register for callback events, you will have to set `PaymentStatusListener` with `EasyUpiPayment` as below.
```java
        easyUpiPayment.setPaymentStatusListener(this);
```
**Description :**

* `onTransactionCompleted()` - This method is invoked when transaction is completed. It may either `SUCCESS` or `FAILED`.
> **NOTE - If onTransactionCompleted() is invoked it doesn't means that payment is successful. It may fail but transaction is completed is the only purpose.**
* `onTransactionSuccess()` - Invoked when Payment is successful.
* `onTransactionFailed()` - Invoked when Payment is unsuccessful/failed.
* `onTransactionCancelled()` - Invoked when Payment cancelled (User pressed back button or any reason).
```java
    @Override
    public void onTransactionCompleted(TransactionDetails transactionDetails) {
        // Transaction Completed
        Log.d("TransactionDetails", transactionDetails.toString());
        statusView.setText(transactionDetails.toString());
    }

    @Override
    public void onTransactionSuccess() {
        // Payment Success
        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        imageView.setImageResource(R.drawable.ic_success);
    }

    @Override
    public void onTransactionFailed() {
        // Payment Failed
        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
        imageView.setImageResource(R.drawable.ic_failed);
    }

    @Override
    public void onTransactionCancelled() {
        // Payment Cancelled by User
        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
        imageView.setImageResource(R.drawable.ic_failed);
    }
```
Hurrah.... We have successfully implemented UPI integration in our Android app.
Thank You!

## Contribute
We can collaboratively make it happen. So if you have any issues, new ideas about implementations then just raise issue and we are open for Pull Requests. Improve and make it happen.
