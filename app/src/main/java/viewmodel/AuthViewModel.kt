package com.example.qlct.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    // 🔐 Đăng ký
    fun register(email: String, pass: String, displayName: String, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val user = result.user
                val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                    this.displayName = displayName
                }
                user?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            onFail(task.exception?.let { translateError(it) } ?: "Lỗi cập nhật tên hiển thị")
                        }
                    }
            }
            .addOnFailureListener { exception ->
                onFail(translateError(exception))
            }
    }

    // 🔑 Đăng nhập
    fun login(email: String, pass: String, onSuccess: () -> Unit, onFail: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFail(translateError(exception))
            }
    }

    private fun translateError(exception: Exception): String {
        // 1. Kiểm tra nếu là lỗi FirebaseAuthException (ưu tiên dùng errorCode)
        if (exception is FirebaseAuthException) {
            return when (exception.errorCode) {
                "ERROR_INVALID_EMAIL", "invalid-email" -> "Địa chỉ email không đúng định dạng"
                "ERROR_WRONG_PASSWORD", "wrong-password" -> "Mật khẩu không chính xác"
                "ERROR_USER_NOT_FOUND", "user-not-found" -> "Tài khoản này chưa được đăng ký"
                "ERROR_USER_DISABLED", "user-disabled" -> "Tài khoản đã bị khóa"
                "ERROR_TOO_MANY_REQUESTS", "too-many-requests" -> "Thử quá nhiều lần. Vui lòng đợi một lát"
                "ERROR_OPERATION_NOT_ALLOWED", "operation-not-allowed" -> "Đăng nhập bằng email/mật khẩu chưa được bật"
                "ERROR_WEAK_PASSWORD", "weak-password" -> "Mật khẩu quá yếu (tối thiểu 6 ký tự)"
                "ERROR_EMAIL_ALREADY_IN_USE", "email-already-in-use" -> "Email này đã được sử dụng"
                "ERROR_INVALID_CREDENTIAL", "invalid-credential", "INVALID_LOGIN_CREDENTIALS" -> "Email hoặc mật khẩu không đúng"
                "ERROR_NETWORK_REQUEST_FAILED", "network-request-failed" -> "Lỗi kết nối mạng"
                else -> "Lỗi xác thực: ${exception.localizedMessage ?: exception.errorCode}"
            }
        }

        // 2. Kiểm tra dựa trên nội dung message (dành cho các lỗi khác)
        val message = exception.localizedMessage ?: exception.message ?: ""
        return when {
            message.contains("network", true) -> "Không có kết nối internet"
            message.contains("password", true) -> "Mật khẩu không hợp lệ"
            message.contains("email", true) && message.contains("badly formatted", true) -> "Email không đúng định dạng"
            message.contains("already in use", true) -> "Email này đã tồn tại"
            message.contains("timeout", true) -> "Hết thời gian kết nối"
            message.isEmpty() -> "Đã xảy ra lỗi không xác định"
            else -> "Lỗi: $message"
        }
    }

    // 👤 kiểm tra đã login chưa
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // 🚪 đăng xuất
    fun logout() {
        auth.signOut()
    }

    // 🆔 lấy user id
    fun getUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun getUserEmail(): String {
        return auth.currentUser?.email ?: "Chưa đăng nhập"
    }

    fun getDisplayName(): String {
        return auth.currentUser?.displayName ?: "Người dùng"
    }

    fun updateDisplayName(newName: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (user != null) {
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                this.displayName = newName
            }
            user.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.let { translateError(it) } ?: "Lỗi không xác định")
                }
            }
        } else {
            onComplete(false, "Không tìm thấy người dùng")
        }
    }

    fun sendPasswordReset(onComplete: (Boolean) -> Unit) {
        val email = auth.currentUser?.email
        if (email != null) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        } else {
            onComplete(false)
        }
    }

    fun changePassword(oldPass: String, newPass: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        val email = user?.email
        if (user != null && email != null) {
            val credential = EmailAuthProvider.getCredential(email, oldPass)
            user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                if (reauthTask.isSuccessful) {
                    user.updatePassword(newPass).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            onComplete(true, null)
                        } else {
                            onComplete(false, updateTask.exception?.let { translateError(it) } ?: "Lỗi cập nhật mật khẩu")
                        }
                    }
                } else {
                    onComplete(false, "Mật khẩu cũ không chính xác")
                }
            }
        } else {
            onComplete(false, "Không tìm thấy người dùng")
        }
    }
}