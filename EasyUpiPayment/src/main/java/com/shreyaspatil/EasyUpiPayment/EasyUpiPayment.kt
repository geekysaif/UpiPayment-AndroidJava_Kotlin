package com.shreyaspatil.easyupipayment

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.shreyaspatil.easyupipayment.EasyUpiPayment.Builder
import com.shreyaspatil.easyupipayment.exception.AppNotFoundException
import com.shreyaspatil.easyupipayment.listener.PaymentStatusListener
import com.shreyaspatil.easyupipayment.model.Payment
import com.shreyaspatil.easyupipayment.model.PaymentApp
import com.shreyaspatil.easyupipayment.ui.PaymentUiActivity

/**
 * Class to implement Easy UPI Payment
 * Use [Builder] to create a new instance.
 */
@Suppress("unused")
class EasyUpiPayment constructor(
	private val mActivity: Activity,
	private val mPayment: Payment
) {

	init {
		if (mActivity is AppCompatActivity) {
			registerLifecycleObserver(mActivity)
		} else {
			Log.w(TAG, """
                Current Activity isn't AppCompatActivity.
                You'll need to call EasyUpiPayment#detachListener() to remove listener.
            """.trimIndent())
		}
	}

	/**
	 * Starts the payment transaction. Calling this method launches the Payment Menu
	 * and shows installed UPI apps in device and let user choose one of them to pay.
	 */
	fun startPayment() {
		// Create Payment Activity Intent
		val payIntent = Intent(mActivity, PaymentUiActivity::class.java).apply {
			putExtra(PaymentUiActivity.EXTRA_KEY_PAYMENT, mPayment)
		}

		// Start Payment Activity
		mActivity.startActivity(payIntent)
	}

	/**
	 * Sets the PaymentStatusListener.
	 *
	 * @param mListener Implementation of PaymentStatusListener
	 */
	fun setPaymentStatusListener(mListener: PaymentStatusListener) {
		Singleton.listener = mListener
	}

	/**
	 * Removes the [PaymentStatusListener] which is already registered.
	 */
	fun removePaymentStatusListener() {
		Singleton.listener = null
	}

	/**
	 * Registers lifecycle observer for [mLifecycleOwner]
	 */
	private fun registerLifecycleObserver(mLifecycleOwner: LifecycleOwner) {
		mLifecycleOwner.lifecycle.addObserver(ActivityLifecycleObserver)
	}

	/**
	 * Automatically removes listener once Lifecycle is stopped or destroyed
	 */
	private object ActivityLifecycleObserver : LifecycleObserver {
		@OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
		fun onDestroyed() {
			Log.d(TAG, "onDestroyed")
			Singleton.listener = null
		}
	}

	/**
	 * Builder for [EasyUpiPayment].
	 */
	class Builder(private val activity: Activity) {

		@set:JvmSynthetic
		lateinit var paymentApp: PaymentApp

		@set:JvmSynthetic
		lateinit var payeeVpa: String

		@set:JvmSynthetic
		lateinit var payeeName: String

		@set:JvmSynthetic
		var payeeMerchantCode: String? = null

		@set:JvmSynthetic
		lateinit var transactionId: String

		@set:JvmSynthetic
		lateinit var transactionRefId: String

		@set:JvmSynthetic
		lateinit var description: String

		@set:JvmSynthetic
		lateinit var amount: String

		/**
		 * Sets default payment app for transaction.
		 *
		 * @param paymentApp Sets default payment app from Enum of [PaymentApp].
		 * For e.g. To start payment with BHIM UPI then use [PaymentApp.BHIM_UPI].
		 *
		 * @return this, for chaining.
		 */
		@JvmOverloads
		fun with(paymentApp: PaymentApp = PaymentApp.ALL): Builder = apply {
			this.paymentApp = paymentApp
		}

		/**
		 * Sets the Payee VPA (e.g. example@vpa, 1234XXX@upi).
		 *
		 * @param vpa Payee VPA address (e.g. example@vpa, 1234XXX@upi).
		 *
		 * @return this, for chaining.
		 */
		fun setPayeeVpa(vpa: String): Builder = apply {
			check(vpa.contains("@")) {
				"Payee VPA address should be valid (For e.g. example@vpa)"
			}
			payeeVpa = vpa
		}

		/**
		 * Sets the Payee Name.
		 *
		 * @param name Payee Name.
		 *
		 * @return this, for chaining.
		 */
		fun setPayeeName(name: String): Builder = apply {
			check(name.isNotBlank()) { "Payee Name Should be Valid!" }
			payeeName = name
		}

		/**
		 * Sets the Merchant Code. If present it should be passed.
		 *
		 * @param merchantCode Payee Merchant code if present it should be passed.
		 *
		 * @return this, for chaining.
		 */
		fun setPayeeMerchantCode(merchantCode: String): Builder = apply {
			check(merchantCode.isNotBlank()) { "Merchant Code Should be Valid!" }
			this.payeeMerchantCode = merchantCode
		}

		/**
		 * Sets the Transaction ID. This field is used in Merchant Payments generated by PSPs.
		 *
		 * @param id field is used in Merchant Payments generated by PSPs.
		 *
		 * @return this, for chaining.
		 */
		fun setTransactionId(id: String): Builder = apply {
			check(id.isNotBlank()) { "Transaction ID Should be Valid!" }
			this.transactionId = id
		}

		/**
		 * Sets the Transaction Reference ID. Transaction reference ID. This could be order number,
		 * subscription number, Bill ID, booking ID, insurance renewal reference, etc.
		 * Needed for merchant transactions and dynamic URL generation.
		 *
		 * @param refId field is used in Merchant Payments generated by PSPs.
		 *
		 * @return this, for chaining.
		 */
		fun setTransactionRefId(refId: String): Builder = apply {
			check(refId.isNotBlank()) { "RefId Should be Valid!" }
			this.transactionRefId = refId
		}

		/**
		 * Sets the Description. It have to provide valid small note or description about payment.
		 * for e.g. For Food
		 *
		 * @param description field have to provide valid small note or description about payment.
		 * for e.g. For Food, For Payment at Shop XYZ
		 *
		 * @return this, for chaining.
		 */
		fun setDescription(description: String): Builder = apply {
			check(description.isNotBlank()) { "Description Should be Valid!" }
			this.description = description
		}

		/**
		 * Sets the Amount in INR. (Format should be decimal e.g. 14.88)
		 *
		 * @param amount field takes amount in String decimal format (xx.xx) to be paid.
		 * For e.g. 90.88 will pay Rs. 90.88.
		 *
		 * @return this, for chaining.
		 */
		fun setAmount(amount: String): Builder = apply {
			check(amount.matches("""\d+\.\d*""".toRegex())) {
				"Amount should be valid positive number and in decimal format XX.XX (For e.g. 100.00)"
			}
			this.amount = amount
		}

		/**
		 * Build the [EasyUpiPayment] object.
		 */
		@Throws(IllegalStateException::class, AppNotFoundException::class)
		fun build(): EasyUpiPayment {
			if (paymentApp != PaymentApp.ALL) {
				if (isPackageInstalled(paymentApp.packageName)) {
					throw AppNotFoundException(paymentApp.packageName)
				}
			}

			check(this::payeeVpa.isInitialized) { "Must call setPayeeVpa() before build()." }
			check(this::transactionId.isInitialized) { "Must call setTransactionId() before build" }
			check(this::transactionRefId.isInitialized) { "Must call setTransactionRefId() before build" }
			check(this::payeeName.isInitialized) { "Must call setPayeeName() before build()." }
			check(this::amount.isInitialized) { "Must call setAmount() before build()." }
			check(this::description.isInitialized) { "Must call setDescription() before build()." }

			val payment = Payment(
				currency = "INR",
				vpa = payeeVpa,
				name = payeeName,
				payeeMerchantCode = payeeMerchantCode,
				txnId = transactionId,
				txnRefId = transactionRefId,
				description = description,
				amount = amount,
				defaultPackage = if (paymentApp != PaymentApp.ALL) paymentApp.packageName else null
			)
			return EasyUpiPayment(activity, payment)
		}

		/**
		 * Check Whether UPI App is installed on device or not
		 *
		 * @return true if app exists, otherwise false.
		 */
		private fun isPackageInstalled(packageName: String): Boolean = runCatching {
			activity.packageManager.getPackageInfo(packageName, 0) != null
		}.getOrDefault(false)
	}

	companion object {
		const val TAG = "EasyUpiPayment"
	}
}

@Suppress("FunctionName")
@JvmSynthetic
fun EasyUpiPayment(activity: Activity, initializer: Builder.() -> Unit): EasyUpiPayment {
	return Builder(activity).apply(initializer).build()
}