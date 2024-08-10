package dev.xinto.argos.local.account

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import platform.CoreFoundation.CFDictionaryAddValue
import platform.CoreFoundation.CFDictionaryCreateMutable
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFRelease
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSDictionary
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithData
import platform.Security.SecItemAdd
import platform.Security.SecItemCopyMatching
import platform.Security.SecItemDelete
import platform.Security.SecItemUpdate
import platform.Security.errSecDuplicateItem
import platform.Security.errSecSuccess
import platform.Security.kSecAttrAccessible
import platform.Security.kSecAttrAccessibleWhenUnlocked
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrKeyType
import platform.Security.kSecAttrKeyTypeRSA
import platform.Security.kSecAttrType
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecReturnData
import platform.Security.kSecValueData

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class ArgosAccountManager {

    private val isLoggedIn = MutableStateFlow(
        getToken() != null && getRefreshToken() != null && getProfileId() != null
    )

    fun fetchLoginStatus() {
        isLoggedIn.update {
            getToken() != null && getRefreshToken() != null && getProfileId() != null
        }
    }

    actual fun isLoggedIn(): Flow<Boolean> = isLoggedIn

    actual fun logout() {
        deleteData(KEY_TOKEN)
        deleteData(KEY_REFRESH_TOKEN)
        deleteData(KEY_PROFILE_ID)
        fetchLoginStatus()
    }

    actual fun getToken(): String? = getData(KEY_TOKEN)
    actual fun getRefreshToken(): String? = getData(KEY_REFRESH_TOKEN)
    actual fun getProfileId(): String? = getData(KEY_PROFILE_ID)

    actual fun setToken(token: String) {
        saveData(KEY_TOKEN, token)
        fetchLoginStatus()
    }

    actual fun setRefreshToken(refreshToken: String) {
        saveData(KEY_REFRESH_TOKEN, refreshToken)
        fetchLoginStatus()
    }

    actual fun setProfileId(profileId: String) {
        saveData(KEY_PROFILE_ID, profileId)
        fetchLoginStatus()
    }

    private fun saveData(account: String, value: String) {
        return memScoped {
            val accountBytes = NSData.dataWithBytes(
                bytes = account.cstr.ptr,
                length = account.length.toULong()
            )
            val valueBytes = NSData.create(
                bytes = value.cstr.ptr,
                length = value.length.toULong()
            )
            val attributes = CFDictionaryCreateMutable(null, 4, null, null).apply {
                CFDictionaryAddValue(this, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(this, kSecAttrAccount, CFBridgingRetain(accountBytes))
                CFDictionaryAddValue(this, kSecValueData, CFBridgingRetain(valueBytes))
                CFDictionaryAddValue(this, kSecAttrAccessible, kSecAttrAccessibleWhenUnlocked)
            }
            val status = SecItemAdd(attributes, null)

            if (status == errSecDuplicateItem) {
                val query = CFDictionaryCreateMutable(null, 2, null, null).apply {
                    CFDictionaryAddValue(this, kSecClass, kSecClassGenericPassword)
                    CFDictionaryAddValue(this, kSecAttrAccount, CFBridgingRetain(accountBytes))
                }
                val updateAttributes = CFDictionaryCreateMutable(null, 1, null, null).apply {
                    CFDictionaryAddValue(this, kSecValueData, CFBridgingRetain(valueBytes))
                }
                SecItemUpdate(query, updateAttributes)
                CFRelease(query)
                CFRelease(updateAttributes)
            }

            CFRelease(attributes)
        }
    }

    private fun getData(account: String): String? {
        return memScoped {
            val query = CFDictionaryCreateMutable(null, 3, null, null).apply {
                val accountBytes = NSData.dataWithBytes(
                    bytes = account.cstr.ptr,
                    length = account.length.toULong()
                )

                CFDictionaryAddValue(this, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(this, kSecAttrAccount, CFBridgingRetain(accountBytes))
                CFDictionaryAddValue(this, kSecReturnData, kCFBooleanTrue)
            }
            val data = memScoped {
                val result = alloc<CFTypeRefVar>()
                val status = SecItemCopyMatching(query, result.ptr)
                if (status != errSecSuccess) {
                    return null
                }

                CFBridgingRelease(result.value) as NSData
            }
            val dataString = NSString.create(data, NSUTF8StringEncoding) as String
            CFBridgingRelease(query)
            dataString
        }
    }

    private fun deleteData(account: String) {
        memScoped {
            val query = CFDictionaryCreateMutable(null, 3, null, null).apply {
                val accountBytes = NSData.dataWithBytes(
                    bytes = account.cstr.ptr,
                    length = account.length.toULong()
                )

                CFDictionaryAddValue(this, kSecClass, kSecClassGenericPassword)
                CFDictionaryAddValue(this, kSecAttrAccount, CFBridgingRetain(accountBytes))
                CFDictionaryAddValue(this, kSecReturnData, kCFBooleanTrue)
            }
            SecItemDelete(query)
        }
    }

    private companion object {
        const val KEY_TOKEN = "argos-token"
        const val KEY_REFRESH_TOKEN = "argos-refresh-token"
        const val KEY_PROFILE_ID = "argos-profile-id"
    }
}