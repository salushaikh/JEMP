package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.*
import com.example.data.service.PayrollCalculationService
import com.example.ui.theme.*

@Composable
fun AddPaymentStageDialog(
    onDismiss: () -> Unit,
    onSubmit: (projectId: String, projectTitle: String, type: PaymentStageType, description: String, amount: Double, currency: String, clientName: String?, invoiceNumber: String?) -> Unit
) {
    var projectId by remember { mutableStateOf("P#014") }
    var projectTitle by remember { mutableStateOf("AI Resume Analyzer") }
    var selectedType by remember { mutableStateOf(PaymentStageType.MILESTONE) }
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("50000") }
    var currency by remember { mutableStateOf("INR") }
    var clientName by remember { mutableStateOf("Nexus Recruit Tech") }
    var invoiceNumber by remember { mutableStateOf("INV-014-004") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("add_payment_stage_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Create Financial Payment Stage",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = projectId,
                    onValueChange = { projectId = it },
                    label = { Text("Project ID (e.g. P#014)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = projectTitle,
                    onValueChange = { projectTitle = it },
                    label = { Text("Project Title") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Stage Type", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(PaymentStageType.ADVANCE, PaymentStageType.MILESTONE, PaymentStageType.FINAL_PAYMENT).forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DiscordBlurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Quoted / Invoiced Amount (₹)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Client Name / Entity") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = invoiceNumber,
                    onValueChange = { invoiceNumber = it },
                    label = { Text("Invoice Reference Number") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Scope & Deliverables Description") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                errorMessage = "Please enter a valid positive amount."
                                return@Button
                            }
                            if (description.isBlank()) {
                                errorMessage = "Please provide scope description."
                                return@Button
                            }
                            onSubmit(projectId, projectTitle, selectedType, description, amt, currency, clientName, invoiceNumber)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        modifier = Modifier.testTag("submit_create_stage_btn")
                    ) {
                        Text("Create Stage", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitProofDialog(
    stageDetail: FinancialStageDetail,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, currency: String, method: PaymentMethodType, paymentDate: String, invoiceRef: String, txnRef: String, desc: String, attachments: List<EvidenceAttachment>, prevIssue: String?) -> Unit
) {
    var amountText by remember { mutableStateOf(stageDetail.expectedAmount.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.BANK_TRANSFER) }
    var invoiceRef by remember { mutableStateOf(stageDetail.invoiceNumber ?: "INV-${stageDetail.projectId}-001") }
    var txnRef by remember { mutableStateOf("TXN-${System.currentTimeMillis().toString().takeLast(6)}") }
    var description by remember { mutableStateOf("Client transferred full payment after milestone sign-off.") }
    var prevIssueAddressed by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val isRevision = stageDetail.status == PaymentStageStatus.PROOF_NEEDS_MORE_EVIDENCE || (stageDetail.currentProof != null)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("submit_proof_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    if (isRevision) "Submit Revised Proof (v${(stageDetail.currentProof?.version ?: 0) + 1})" else "Submit Financial Proof [${stageDetail.stageCode}]",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "All contributors must review and verify banking proof before distribution.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Exact Amount Received (${stageDetail.currency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(PaymentMethodType.BANK_TRANSFER, PaymentMethodType.UPI, PaymentMethodType.WIRE_TRANSFER).forEach { m ->
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DiscordBlurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = invoiceRef,
                    onValueChange = { invoiceRef = it },
                    label = { Text("Invoice Reference") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = txnRef,
                    onValueChange = { txnRef = it },
                    label = { Text("Transaction Reference / Bank UTR") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (isRevision) {
                    OutlinedTextField(
                        value = prevIssueAddressed,
                        onValueChange = { prevIssueAddressed = it },
                        label = { Text("Explanation of Feedback Addressed") },
                        minLines = 2,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = DiscordBlurple,
                            unfocusedBorderColor = DarkBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Transaction Notes / Statement Details") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Evidence Attachments Box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = DarkBackground,
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AttachFile, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Attached Evidence Proofs (2 Files Auto-Attached)", style = MaterialTheme.typography.labelMedium, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("• $invoiceRef.pdf (Official Invoiced Bill)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                        Text("• BankStatement_Settlement_${txnRef}.png (Bank Receiving Proof)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                    }
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorText!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                errorText = "Please enter valid amount."
                                return@Button
                            }
                            val attachments = listOf(
                                EvidenceAttachment("$invoiceRef.pdf", "invoice_pdf", "https://evidence.joblessempire.org/$invoiceRef", 128, "sha256_inv_$invoiceRef"),
                                EvidenceAttachment("BankReceipt_$txnRef.png", "bank_receipt", "https://evidence.joblessempire.org/$txnRef", 345, "sha256_bank_$txnRef")
                            )
                            onSubmit(amt, stageDetail.currency, selectedMethod, "Today", invoiceRef, txnRef, description, attachments, prevIssueAddressed.ifBlank { null })
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        modifier = Modifier.testTag("submit_proof_confirm_btn")
                    ) {
                        Text(if (isRevision) "Submit Revision" else "Submit Proof", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun CreateDistributionProposalDialog(
    stageDetail: FinancialStageDetail,
    onDismiss: () -> Unit,
    onSubmit: (rationale: String, allocations: List<Triple<String, String, Double>>) -> Unit
) {
    var rationale by remember {
        mutableStateOf("Alex (Lead 40% - Architecture & ML), Sarah (35% - Compose UI & State), John (25% - API & Testing).")
    }

    var p1Percent by remember { mutableStateOf("40") }
    var p2Percent by remember { mutableStateOf("35") }
    var p3Percent by remember { mutableStateOf("25") }

    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("create_distribution_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Create Distribution Proposal",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "5% Admin Fee is auto-deducted. Distributable pool (95%) must sum to exactly 100.00%.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Contributor 1 (Owner)
                OutlinedTextField(
                    value = p1Percent,
                    onValueChange = { p1Percent = it },
                    label = { Text("Alex (Project Owner) Share %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contributor 2
                OutlinedTextField(
                    value = p2Percent,
                    onValueChange = { p2Percent = it },
                    label = { Text("Sarah (UI / Frontend) Share %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contributor 3
                OutlinedTextField(
                    value = p3Percent,
                    onValueChange = { p3Percent = it },
                    label = { Text("John (QA / Testing) Share %") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = rationale,
                    onValueChange = { rationale = it },
                    label = { Text("Mandatory Contribution Rationale") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                val sum = (p1Percent.toDoubleOrNull() ?: 0.0) + (p2Percent.toDoubleOrNull() ?: 0.0) + (p3Percent.toDoubleOrNull() ?: 0.0)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Percentage: ${String.format(java.util.Locale.US, "%.1f", sum)}%", color = if (Math.abs(sum - 100.0) < 0.01) AccentGreen else AccentRed, fontWeight = FontWeight.Bold)
                    Text(if (Math.abs(sum - 100.0) < 0.01) "✅ Balanced" else "❌ Needs 100.0%", color = if (Math.abs(sum - 100.0) < 0.01) AccentGreen else AccentRed, style = MaterialTheme.typography.labelSmall)
                }

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorText!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val v1 = p1Percent.toDoubleOrNull() ?: 0.0
                            val v2 = p2Percent.toDoubleOrNull() ?: 0.0
                            val v3 = p3Percent.toDoubleOrNull() ?: 0.0
                            val total = v1 + v2 + v3
                            if (Math.abs(total - 100.0) > 0.01) {
                                errorText = "Allocations must total exactly 100.0%. Current sum: $total%"
                                return@Button
                            }
                            val allocs = listOf(
                                Triple("JEMP#014", "Alex", v1),
                                Triple("JEMP#022", "Sarah", v2),
                                Triple("JEMP#031", "John", v3)
                            )
                            onSubmit(rationale, allocs)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        modifier = Modifier.testTag("submit_distribution_confirm_btn")
                    ) {
                        Text("Submit Proposal", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun SubmitOwnerTransferDialog(
    stageDetail: FinancialStageDetail,
    onDismiss: () -> Unit,
    onSubmit: (method: PaymentMethodType, amount: Double, txnRef: String, cryptoHash: String?, notes: String?) -> Unit
) {
    val proposal = stageDetail.currentDistribution
    val gross = proposal?.grossAmount ?: stageDetail.expectedAmount
    val ownerAlloc = proposal?.allocations?.find { it.memberId == stageDetail.projectOwnerMemberId }
    val ownerShare = ownerAlloc?.calculatedAmount ?: (gross * 0.40)
    val requiredTransfer = gross - ownerShare

    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI) }
    var amountText by remember { mutableStateOf(requiredTransfer.toInt().toString()) }
    var txnRef by remember { mutableStateOf("UPI-ADMIN-${System.currentTimeMillis().toString().takeLast(6)}") }
    var notes by remember { mutableStateOf("Transferred team pool + admin fee to Admin Escrow.") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("submit_owner_transfer_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Submit Transfer to Admin Escrow",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    "Formula: Required = Gross (${PayrollCalculationService.formatCurrency(gross, stageDetail.currency)}) - Owner Share (${PayrollCalculationService.formatCurrency(ownerShare, stageDetail.currency)}) = ${PayrollCalculationService.formatCurrency(requiredTransfer, stageDetail.currency)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AccentCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Transfer Amount (${stageDetail.currency})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(PaymentMethodType.UPI, PaymentMethodType.BANK_TRANSFER, PaymentMethodType.CRYPTO).forEach { m ->
                        FilterChip(
                            selected = selectedMethod == m,
                            onClick = { selectedMethod = m },
                            label = { Text(m.label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = DiscordBlurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = txnRef,
                    onValueChange = { txnRef = it },
                    label = { Text("Transfer UTR / Transaction ID") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Transfer Notes / Remarks") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorText!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull()
                            if (amt == null || amt <= 0.0) {
                                errorText = "Please enter valid amount."
                                return@Button
                            }
                            onSubmit(selectedMethod, amt, txnRef, null, notes)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DiscordBlurple),
                        modifier = Modifier.testTag("submit_transfer_confirm_btn")
                    ) {
                        Text("Confirm Transfer Sent", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewFeedbackDialog(
    title: String,
    actionLabel: String,
    isApproval: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (reason: String?) -> Unit
) {
    var reason by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("review_feedback_dialog"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    if (!isApproval) "A detailed and specific reason is required." else "Provide any optional comments for the audit log.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = reason,
                    onValueChange = { reason = it },
                    label = { Text(if (isApproval) "Reviewer Notes (Optional)" else "Reason for Objection (Mandatory)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = DiscordBlurple,
                        unfocusedBorderColor = DarkBorder
                    )
                )

                if (errorText != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorText!!, color = AccentRed, style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (!isApproval && reason.isBlank()) {
                                errorText = "Reason cannot be empty for objections."
                                return@Button
                            }
                            onSubmit(reason.ifBlank { null })
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isApproval) AccentGreen else AccentRed
                        )
                    ) {
                        Text(actionLabel, color = Color.White)
                    }
                }
            }
        }
    }
}
