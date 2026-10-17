package com.example.ui.login

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

private const val SUPPORT_EMAIL = "adffakff@gmail.com"
private const val BACKUP_SUPPORT_EMAIL = "join@quran-society.org"

/**
 * قسم "تواصل معنا - طلب انضمام جمعية للنظام"
 * يتيح لزوار المنظومة تقديم طلب انضمام لجمعيتهم أو مركزهم القرآني
 * مع ربط الطلب بالبريد الإلكتروني وتجهيز رسالة منظمة بكل تفاصيل الجمعية.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactUsJoinSection(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Expanded / Collapsed state
    var isExpanded by remember { mutableStateOf(true) }

    // Form Fields
    var societyName by remember { mutableStateOf("") }
    var governorateAndCity by remember { mutableStateOf("") }
    var contactPersonName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var notesOrRingsCount by remember { mutableStateOf("") }

    // Validation & Dialogs
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showNoEmailAppDialog by remember { mutableStateOf(false) }

    fun sendJoinRequestViaEmail() {
        if (societyName.trim().isEmpty()) {
            errorMessage = "يرجى كتابة اسم الجمعية أو المركز القرآني."
            return
        }
        if (contactPersonName.trim().isEmpty()) {
            errorMessage = "يرجى كتابة اسم المشرف أو مسؤول التواصل."
            return
        }
        if (phoneNumber.trim().isEmpty() && contactEmail.trim().isEmpty()) {
            errorMessage = "يرجى إدخال رقم هاتف أو بريد إلكتروني للتواصل معكم."
            return
        }

        errorMessage = null

        val subject = "طلب انضمام جمعية جديدة: ${societyName.trim()} (${if (governorateAndCity.isNotBlank()) governorateAndCity.trim() else "اليمن"})"
        val bodyContent = buildString {
            appendLine("السلام عليكم ورحمة الله وبركاته،")
            appendLine()
            appendLine("نود تقديم طلب انضمام جمعيتنا / مركزنا القرآني إلى المنظومة الإلكترونية الموحدة لإدارة حلقات تحفيظ القرآن الكريم.")
            appendLine()
            appendLine("══════════════════════════════════════")
            appendLine("تفاصيل طلب الانضمام:")
            appendLine("• اسم الجمعية / المركز: ${societyName.trim()}")
            appendLine("• المحافظة والمدينة: ${if (governorateAndCity.isNotBlank()) governorateAndCity.trim() else "غير محدد"}")
            appendLine("• اسم مسؤول التواصل: ${contactPersonName.trim()}")
            appendLine("• رقم الهاتف / واتساب: ${if (phoneNumber.isNotBlank()) phoneNumber.trim() else "غير مدخل"}")
            appendLine("• البريد الإلكتروني للمسؤول: ${if (contactEmail.isNotBlank()) contactEmail.trim() else "غير مدخل"}")
            appendLine("• عدد الحلقات والملاحظات الإضافية: ${if (notesOrRingsCount.isNotBlank()) notesOrRingsCount.trim() else "لا يوجد"}")
            appendLine("══════════════════════════════════════")
            appendLine()
            appendLine("نأمل منكم التكرم بمراجعة بيانات الجمعية واعتماد حساب المنشأة وتزويدنا ببيانات تسجيل الدخول للوحة المشرف العام.")
            appendLine("شاكرين لكم جهودكم المباركة في خدمة كتاب الله تعالى.")
        }

        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:$SUPPORT_EMAIL")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(SUPPORT_EMAIL, BACKUP_SUPPORT_EMAIL))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, bodyContent)
        }

        try {
            val chooser = Intent.createChooser(emailIntent, "إرسال طلب الانضمام عبر تطبيق البريد")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            showSuccessDialog = true
        } catch (e: ActivityNotFoundException) {
            showNoEmailAppDialog = true
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح تطبيق البريد: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            showNoEmailAppDialog = true
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .widthIn(max = 520.dp)
            .testTag("contact_us_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.dp, SocietyBluePrimary.copy(alpha = 0.15f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(SocietyBluePrimary, EmeraldGreen)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MailOutline,
                            contentDescription = "تواصل معنا",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "تواصل معنا | طلب انضمام جمعية",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark
                        )
                        Text(
                            text = "إضافة جمعيتكم إلى المنظومة الإلكترونية",
                            fontSize = 11.sp,
                            color = SocietyBluePrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Toggle Expand/Collapse Button
                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier
                        .testTag("contact_us_toggle_button")
                        .size(38.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "طي النموذج" else "فتح النموذج",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "هل تشرف على جمعية أو مركز قرآني وترغب بربطه بالمنظومة وإصدار لوحة تحكم متكاملة؟ أرسل طلبك وسيتواصل معك الدعم الفني.",
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp
            )

            // Direct support email pill badge
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                color = SocietyBlueLight,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, SocietyBluePrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Email,
                            contentDescription = null,
                            tint = SocietyBluePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "البريد المعتمد: $SUPPORT_EMAIL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SocietyBluePrimary
                        )
                    }
                    TextButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(SUPPORT_EMAIL))
                            Toast.makeText(context, "تم نسخ البريد الإلكتروني", Toast.LENGTH_SHORT).show()
                        },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("copy_support_email_btn")
                    ) {
                        Text("نسخ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SocietyBluePrimary)
                    }
                }
            }

            // Expandable Form
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    Divider(color = BorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(14.dp))

                    // Error Message Banner
                    if (errorMessage != null) {
                        Surface(
                            color = Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("contact_error_banner")
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "خطأ",
                                    tint = StatusError,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage.orEmpty(),
                                    fontSize = 12.sp,
                                    color = StatusError,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // الحقل 1: اسم الجمعية أو المركز
                    OutlinedTextField(
                        value = societyName,
                        onValueChange = {
                            societyName = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("اسم الجمعية / المركز القرآني *", fontSize = 12.sp) },
                        placeholder = { Text("مثال: جمعية معاذ العلمية أو دار الفرقان", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = SocietyBluePrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_society_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // الحقل 2: المحافظة والمدينة
                    OutlinedTextField(
                        value = governorateAndCity,
                        onValueChange = { governorateAndCity = it },
                        label = { Text("المحافظة والمدينة / الفرع", fontSize = 12.sp) },
                        placeholder = { Text("مثال: تعز، صنعاء، عدن، مأرب، حضرموت...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = EmeraldGreen)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_city_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // الحقل 3: اسم المشرف أو مسؤول التواصل
                    OutlinedTextField(
                        value = contactPersonName,
                        onValueChange = {
                            contactPersonName = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("اسم المشرف أو مسؤول التواصل *", fontSize = 12.sp) },
                        placeholder = { Text("مثال: الشيخ / أ. عبد الله محمد", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = SocietyBluePrimary)
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_person_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // الحقل 4: رقم الجوال أو الواتساب
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = {
                            phoneNumber = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("رقم الجوال أو الواتساب *", fontSize = 12.sp) },
                        placeholder = { Text("مثال: 770123456 أو +967...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = GoldenAmber)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_phone_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // الحقل 5: البريد الإلكتروني للمسؤول
                    OutlinedTextField(
                        value = contactEmail,
                        onValueChange = {
                            contactEmail = it
                            if (errorMessage != null) errorMessage = null
                        },
                        label = { Text("البريد الإلكتروني للجمعية / المسؤول", fontSize = 12.sp) },
                        placeholder = { Text("info@quran-society.org", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = SocietyBluePrimary)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_email_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // الحقل 6: عدد الحلقات وملاحظات إضافية
                    OutlinedTextField(
                        value = notesOrRingsCount,
                        onValueChange = { notesOrRingsCount = it },
                        label = { Text("عدد الحلقات التقريبي / ملاحظات إضافية", fontSize = 12.sp) },
                        placeholder = { Text("مثال: 12 حلقة للبنين و 8 للبنات، 250 طالباً...", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Notes, contentDescription = null, tint = TextMuted)
                        },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("contact_notes_input"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SocietyBluePrimary,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // زر إرسال الطلب عبر البريد الإلكتروني
                    Button(
                        onClick = { sendJoinRequestViaEmail() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("contact_submit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SocietyBluePrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إرسال طلب الانضمام عبر البريد الإلكتروني",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // زر إرسال بديل مباشر عبر تطبيق Gmail / البريد
                    OutlinedButton(
                        onClick = {
                            val directIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$SUPPORT_EMAIL?subject=${Uri.encode("استفسار عن انضمام جمعية للنظام")}")
                            }
                            try {
                                context.startActivity(directIntent)
                            } catch (e: Exception) {
                                clipboardManager.setText(AnnotatedString(SUPPORT_EMAIL))
                                Toast.makeText(context, "تم نسخ البريد: $SUPPORT_EMAIL", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("contact_direct_email_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, SocietyBluePrimary.copy(alpha = 0.4f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = SocietyBluePrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Drafts,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "مراسلة مباشرة إلى $SUPPORT_EMAIL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

    // Modal: Confirmation Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            icon = {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(EmeraldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "نجاح",
                        tint = StatusSuccess,
                        modifier = Modifier.size(32.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "تم فتح تطبيق البريد لإرسال الطلب",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "تم تجهيز رسالة طلب انضمام جمعية \"$societyName\" ونقلها إلى تطبيق البريد الإلكتروني مع كافة البيانات والتفاصيل.",
                        fontSize = 13.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "الوجهة: $SUPPORT_EMAIL\nسيقوم فريق إدارة النظام بمراجعة الطلب والتواصل معكم فوراً.",
                        fontSize = 12.sp,
                        color = SocietyBluePrimary,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        // Optional reset
                        societyName = ""
                        governorateAndCity = ""
                        contactPersonName = ""
                        phoneNumber = ""
                        contactEmail = ""
                        notesOrRingsCount = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SocietyBluePrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("contact_success_dismiss_button")
                ) {
                    Text("تم، حسناً")
                }
            }
        )
    }

    // Modal: No Email App Installed Fallback
    if (showNoEmailAppDialog) {
        AlertDialog(
            onDismissRequest = { showNoEmailAppDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "تنبيه",
                    tint = GoldenAmber,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "بيانات التواصل المباشر",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "تعذر العثور على تطبيق بريد افتراضي مثبت على هذا الجهاز. يمكنك إرسال بيانات جمعيتكم يدوياً إلى البريد التالي:",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = SocietyBlueLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = SUPPORT_EMAIL,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = SocietyBluePrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(SUPPORT_EMAIL))
                        Toast.makeText(context, "تم نسخ البريد بنجاح: $SUPPORT_EMAIL", Toast.LENGTH_SHORT).show()
                        showNoEmailAppDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SocietyBluePrimary),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("نسخ البريد الإلكتروني")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNoEmailAppDialog = false }) {
                    Text("إغلاق")
                }
            }
        )
    }
}
