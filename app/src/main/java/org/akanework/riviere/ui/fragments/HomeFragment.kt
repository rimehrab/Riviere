package org.akanework.riviere.ui.fragments

import android.animation.ValueAnimator
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.text.InputFilter
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.slider.Slider
import org.akanework.riviere.R
import org.akanework.riviere.logic.px
import org.akanework.riviere.logic.utils.DecimalDigitsInputFilter
import org.akanework.riviere.ui.adapters.HomeChipAdapter
import org.akanework.riviere.ui.adapters.HomePresetAdapter
import org.akanework.riviere.ui.adapters.TransactionAdapter
import org.akanework.riviere.ui.data.HolderTypes


class HomeFragment : BaseFragment() {

    companion object {
        const val ACTION_BUTTON_ANIMATION_DURATION: Long = 300
    }

    private lateinit var prefs: SharedPreferences

    private lateinit var formatSwitchButton: MaterialButton
    private lateinit var targetExpenseEditText: EditText

    private var colorPlusPrimaryContainer: Int = -1
    private var colorPlusOnPrimaryContainer: Int = -1
    private var colorMinusPrimaryContainer: Int = -1
    private var colorMinusOnPrimaryContainer: Int = -1

    private lateinit var roundCounterTextView: TextView
    private lateinit var decimalCounterTextView: TextView
    private lateinit var currencyIcon: TextView
    private lateinit var incomeTextView: TextView
    private lateinit var budgetTextView: TextView
    private lateinit var headerIncomeTextView: TextView
    private lateinit var headerExpenseTextView: TextView

    private val transactions = mutableListOf<HolderTypes.TransactionEntry>()
    private var selectedChipIndex = 0
    private var budgetLimit = 5000f
    private lateinit var transactionAdapter: TransactionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setBackgroundColor(
            MaterialColors.getColor(view, com.google.android.material.R.attr.colorSurfaceContainer)
        )
    }

    @Suppress("DEPRECATION")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("TAG", "HOMEFRAGMENT ONCREATE")

        prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())

        val rootView = inflater.inflate(R.layout.fragment_home, container, false)

        // Upper part
        val presetRecyclerView: RecyclerView = rootView.findViewById(R.id.preset_recyclerview)
        val chipRecyclerView: RecyclerView = rootView.findViewById(R.id.chips_recyclerview)
        targetExpenseEditText = rootView.findViewById(R.id.target_expense)

        formatSwitchButton = rootView.findViewById(R.id.plus_minus_switch)

        val recordButton: MaterialButton = rootView.findViewById(R.id.record_button)
        val quickRecordText: TextView = rootView.findViewById(R.id.quick_record_text)
        val newPresetButton: MaterialButton = rootView.findViewById(R.id.new_preset)
        val currencyTextView: TextView = rootView.findViewById(R.id.value_currency)

        // Bottom part
        val bottomSheet: FrameLayout = rootView.findViewById(R.id.bottom_sheet)
        val standardBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        val bottomSheetHeaderCard: MaterialCardView = bottomSheet.findViewById(R.id.bottom_header)
        val expenseTitleTextView: TextView = bottomSheet.findViewById(R.id.expense_title)
        currencyIcon = bottomSheet.findViewById(R.id.currency)
        roundCounterTextView = bottomSheet.findViewById(R.id.round)
        decimalCounterTextView = bottomSheet.findViewById(R.id.decimal)
        val incomeTitleTextView: TextView = bottomSheet.findViewById(R.id.income_title)
        incomeTextView = bottomSheet.findViewById(R.id.income)
        val budgetTitleTextView: TextView = bottomSheet.findViewById(R.id.budget_title)
        budgetTextView = bottomSheet.findViewById(R.id.budget)
        val budgetSlider: Slider = bottomSheet.findViewById(R.id.budget_slider)

        headerIncomeTextView = rootView.findViewById(R.id.header_income_total)
        headerExpenseTextView = rootView.findViewById(R.id.header_expense_total)

        val transactionRecyclerView: RecyclerView = rootView.findViewById(R.id.transaction_recyclerview)
        transactionAdapter = TransactionAdapter(transactions)
        transactionRecyclerView.adapter = transactionAdapter
        transactionRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Get colors
        val colorPositiveBottomHeaderPrimaryContainer =
            MaterialColors.harmonizeWithPrimary(
                requireContext(),
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bottom_sheet_colorPrimaryContainer)
            )
        val colorPositiveBottomHeaderOnPrimaryContainer =
            MaterialColors.harmonizeWithPrimary(
                requireContext(),
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bottom_sheet_colorOnPrimaryContainer)
            )
        val colorPositiveBottomHeaderPrimaryInverse =
            MaterialColors.harmonizeWithPrimary(
                requireContext(),
                ContextCompat.getColor(
                    requireContext(),
                    R.color.bottom_sheet_colorPrimaryInverse)
            )
        colorPlusPrimaryContainer =
            MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorPrimary,
                -1
            )
        colorPlusOnPrimaryContainer =
            MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorOnPrimary,
                -1
            )
        colorMinusPrimaryContainer =
            MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorTertiary,
                -1
            )
        colorMinusOnPrimaryContainer =
            MaterialColors.getColor(
                requireContext(),
                com.google.android.material.R.attr.colorOnTertiary,
                -1
            )

        setUpSwitchButton()
        setupUpperPart()

        // Dispatch colors
        bottomSheetHeaderCard.setCardBackgroundColor(
            colorPositiveBottomHeaderPrimaryContainer
        )
        expenseTitleTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        currencyIcon.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        roundCounterTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        decimalCounterTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        incomeTitleTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        incomeTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        budgetTitleTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        budgetTextView.setTextColor(
            colorPositiveBottomHeaderOnPrimaryContainer
        )
        budgetSlider.trackActiveTintList =
            ColorStateList.valueOf(colorPositiveBottomHeaderOnPrimaryContainer)
        budgetSlider.trackInactiveTintList =
            ColorStateList.valueOf(colorPositiveBottomHeaderPrimaryInverse)

        val displayMetrics = DisplayMetrics()

        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)

        val halfScreenHeight = displayMetrics.heightPixels - 324.px

        standardBottomSheetBehavior.peekHeight = halfScreenHeight

        standardBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {}
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        budgetSlider.addOnChangeListener { _, value, _ ->
            budgetLimit = value * 10000f
            refreshBottomSheet()
        }

        val presetData = mutableListOf(
            HolderTypes.PresetType(null, "星巴克", -50f, isBlock = false),
            HolderTypes.PresetType(isBlock = true)
        )
        presetRecyclerView.adapter = HomePresetAdapter(
            presetData,
            requireContext(),
            onItemClick = { preset ->
                if (preset.isBlock) {
                    Toast.makeText(requireContext(), "Add preset coming soon", Toast.LENGTH_SHORT).show()
                } else {
                    val absValue = Math.abs(preset.defVal ?: 0f)
                    targetExpenseEditText.setText(absValue.toString())
                    preset.currencyType?.let { currencyTextView.text = it.toString() }
                    val shouldBeIncome = (preset.defVal ?: 0f) > 0
                    if (shouldBeIncome != prefs.getBoolean("switch_button_state", true)) {
                        setButtonStatus(shouldBeIncome, true)
                        prefs.edit().putBoolean("switch_button_state", shouldBeIncome).apply()
                    }
                }
            }
        )
        presetRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val chipData = mutableListOf(
            HolderTypes.ChipType(R.drawable.ic_category_filled, "默认"),
            HolderTypes.ChipType(),
            HolderTypes.ChipType(isBlock = true)
        )
        val chipAdapter = HomeChipAdapter(
            chipData,
            requireContext(),
            onChipSelected = { index, chip ->
                if (chip.isBlock) {
                    Toast.makeText(requireContext(), "Add category coming soon", Toast.LENGTH_SHORT).show()
                } else {
                    selectedChipIndex = index
                }
            }
        )
        chipRecyclerView.adapter = chipAdapter
        chipRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val recordAction = {
            val rawText = targetExpenseEditText.text.toString()
            val amount = rawText.toFloatOrNull()
            if (amount != null && amount != 0f) {
                val isIncome = prefs.getBoolean("switch_button_state", true)
                val categoryName = if (chipAdapter.selectedIndex < chipData.size) {
                    chipData[chipAdapter.selectedIndex].desc ?: "未分类"
                } else {
                    "未分类"
                }
                transactions.add(HolderTypes.TransactionEntry(amount, isIncome, categoryName, null))
                targetExpenseEditText.text.clear()
                transactionAdapter.notifyDataSetChanged()
                refreshBottomSheet()
            }
        }

        recordButton.setOnClickListener { recordAction() }
        quickRecordText.setOnClickListener { recordAction() }
        newPresetButton.setOnClickListener {
            Toast.makeText(requireContext(), "Add preset coming soon", Toast.LENGTH_SHORT).show()
        }

        refreshBottomSheet()

        return rootView
    }

    private fun setUpSwitchButton() {
        val formatSwitchButtonIsPlus = prefs.getBoolean("switch_button_state", true)
        setButtonStatus(formatSwitchButtonIsPlus, false)
        formatSwitchButton.setOnClickListener {
            val status = prefs.getBoolean("switch_button_state", true)
            setButtonStatus(
                !status, true
            )
            prefs.edit()
                .putBoolean("switch_button_state", !status)
                .apply()
        }
    }

    private fun setButtonStatus(isPlus: Boolean, withAnimation: Boolean) {
        if (!withAnimation) {
            formatSwitchButton.iconTint =
                ColorStateList.valueOf(
                    if (isPlus) colorPlusOnPrimaryContainer else colorMinusOnPrimaryContainer
                )
            formatSwitchButton.backgroundTintList =
                ColorStateList.valueOf(
                    if (isPlus) colorPlusPrimaryContainer else colorMinusPrimaryContainer
                )
            formatSwitchButton.icon =
                AppCompatResources.getDrawable(
                    requireContext(),
                    if (isPlus) R.drawable.minus_to_plus else R.drawable.plus_to_minus
                )
        } else {
            val backgroundAnimator = ValueAnimator.ofArgb(
                if (isPlus) colorMinusPrimaryContainer else colorPlusPrimaryContainer,
                if (isPlus) colorPlusPrimaryContainer else colorMinusPrimaryContainer
            )
            val iconAnimator = ValueAnimator.ofArgb(
                if (isPlus) colorMinusOnPrimaryContainer else colorPlusOnPrimaryContainer,
                if (isPlus) colorPlusOnPrimaryContainer else colorMinusOnPrimaryContainer
            )
            backgroundAnimator.apply {
                addUpdateListener {
                    val color = it.animatedValue as Int
                    formatSwitchButton.backgroundTintList =
                        ColorStateList.valueOf(color)
                }
                duration = ACTION_BUTTON_ANIMATION_DURATION
            }
            iconAnimator.apply {
                addUpdateListener {
                    val color = it.animatedValue as Int
                    formatSwitchButton.iconTint =
                        ColorStateList.valueOf(color)
                }
                duration = ACTION_BUTTON_ANIMATION_DURATION
            }
            formatSwitchButton.icon =
                AppCompatResources.getDrawable(
                    requireContext(),
                    if (isPlus) R.drawable.minus_to_plus else R.drawable.plus_to_minus
                )
            backgroundAnimator.start()
            iconAnimator.start()
            (formatSwitchButton.icon as AnimatedVectorDrawable).start()
        }
    }

    private fun refreshBottomSheet() {
        val totalExpense = transactions.filter { !it.isIncome }.sumOf { it.amount.toDouble() }.toFloat()
        val totalIncome = transactions.filter { it.isIncome }.sumOf { it.amount.toDouble() }.toFloat()
        val balance = totalIncome - totalExpense

        setBalanceNumber(balance)

        incomeTextView.text = "+${formatAmount(totalIncome)}$"
        budgetTextView.text = "-${formatAmount(totalExpense)}$"

        headerIncomeTextView.text = "+${formatAmount(totalIncome)}$"
        headerExpenseTextView.text = "-${formatAmount(totalExpense)}$"
    }

    private fun setBalanceNumber(value: Float) {
        val absValue = Math.abs(value)
        val formatted = String.format("%.2f", absValue)
        val parts = formatted.split(".")
        val prefix = if (value < 0) "-" else ""
        roundCounterTextView.text = "$prefix${parts[0]}."
        decimalCounterTextView.text = parts.getOrElse(1) { "00" }
    }

    private fun formatAmount(value: Float): String = String.format("%.2f", value)

    private fun setupUpperPart() {
        targetExpenseEditText.setFilters(
            arrayOf<InputFilter>(
                DecimalDigitsInputFilter(2)
            )
        )
    }

}