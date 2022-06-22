package com.ghtk.internal.ekyc.ui.card.step;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.ghtk.internal.base.remote.OnLoadDataCompleted;
import com.ghtk.logger.LogUtils;
import com.ghtk.internal.base.factory.MoshiFactory;
import com.ghtk.internal.base.fragment.BaseFragment;
import com.ghtk.internal.base.fragment.BaseViewModel;
import com.ghtk.internal.model.remote.BaseRemoteObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.ghtk.internal.utils.factory.FragmentUtils;
import com.ghtk.internal.ekyc.sdk.card.EKycCardIdService;
import com.ghtk.internal.ekyc.sdk.card.model.CardInfoKycDto;
import com.ghtk.internal.ekyc.ui.R;
import com.ghtk.internal.ekyc.ui.databinding.FragmentKycIdentityCardStep2Binding;
import com.ghtk.internal.utils.callbacks.OnSingleClickListener;

import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_EKYC_ID;
import static com.ghtk.internal.ekyc.ui.otp.KycPhoneStep1Fragment.KEY_PHONE;

// step 2 upload CMT
public class KycCardStep2Fragment extends BaseFragment<BaseViewModel, FragmentKycIdentityCardStep2Binding> {

    private CardInfoKycDto cardInfoKycDto = null;

    private String phoneNumber = "";

    @Override
    public int getIdLayout() {
        return R.layout.fragment_kyc_identity_card_step2;
    }

    @Override
    public void doViewCreated(View view) {
        initData();

        initEvents();
    }

    private void initData() {
        if (getArguments() != null) {
            String json = getArguments().getString(KycCardStep1Fragment.KEY_CARD_INFO_SYC_DTO);
            phoneNumber = getArguments().getString(KEY_PHONE);
            MoshiFactory.getDataFromJsonObject(json, CardInfoKycDto.class, new com.ghtk.internal.base.remote.OnLoadDataCompleted<CardInfoKycDto>() {
                @Override
                public void onFinish(CardInfoKycDto result) {
                    cardInfoKycDto = result;
                    updateUIProfileID(result);
                }
            });
        }
    }

    private void initEvents() {
        // ngày cấp
        binding.tvIdentityCardDate.setOnClickListener(v -> {
            if (cardInfoKycDto == null || cardInfoKycDto.getData() == null) {
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            // date
            Calendar calendarDate = Calendar.getInstance();
            try {
                Date dateFormat = simpleDateFormat.parse(cardInfoKycDto.getData().getCardData().getDate());
                if (dateFormat != null) {
                    calendarDate.setTime(dateFormat);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.getMessage());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, year, monthOfYear, dayOfMonth) -> {
                int monthFormat = monthOfYear + 1;
                String monthOfYearString;
                if (monthFormat < 10) {
                    monthOfYearString = "0" + monthFormat;
                } else {
                    monthOfYearString = "" + monthFormat;
                }

                String dayOfMonthString;
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + dayOfMonth;
                } else {
                    dayOfMonthString = "" + dayOfMonth;
                }

                String dateFinal = dayOfMonthString + "/" + monthOfYearString + "/" + year;
                binding.tvIdentityCardDate.setText(dateFinal);
                cardInfoKycDto.getData().getCardData().setDate(dateFinal);

            }, calendarDate.get(Calendar.YEAR), calendarDate.get(Calendar.MONTH), calendarDate.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        // ngày hết hạn
        binding.tvIdentityCardOutDate.setOnClickListener(v -> {
            if (cardInfoKycDto == null || cardInfoKycDto.getData() == null) {
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            // outdate
            Calendar calendarOutDate = Calendar.getInstance();
            try {
                Date dateFormat = simpleDateFormat.parse(cardInfoKycDto.getData().getCardData().getExpiredDate());
                if (dateFormat != null) {
                    calendarOutDate.setTime(dateFormat);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.getMessage());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, year, monthOfYear, dayOfMonth) -> {
                int monthFormat = monthOfYear + 1;
                String monthOfYearString;
                if (monthFormat < 10) {
                    monthOfYearString = "0" + monthFormat;
                } else {
                    monthOfYearString = "" + monthFormat;
                }

                String dayOfMonthString;
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + dayOfMonth;
                } else {
                    dayOfMonthString = "" + dayOfMonth;
                }

                String dateFinal = dayOfMonthString + "/" + monthOfYearString + "/" + year;
                binding.tvIdentityCardOutDate.setText(dateFinal);
                cardInfoKycDto.getData().getCardData().setExpiredDate(dateFinal);

            }, calendarOutDate.get(Calendar.YEAR), calendarOutDate.get(Calendar.MONTH), calendarOutDate.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        // ngày sinh nhật
        binding.tvIdentityCardBirthDay.setOnClickListener(v -> {
            if (cardInfoKycDto == null || cardInfoKycDto.getData() == null) {
                return;
            }

            // ngày sinh nhật
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Calendar calendarBirth = Calendar.getInstance();
            try {
                Date dateFormat = simpleDateFormat.parse(cardInfoKycDto.getData().getCardData().getDob());
                if (dateFormat != null) {
                    calendarBirth.setTime(dateFormat);
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.d(e.getMessage());
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (datePicker, year, monthOfYear, dayOfMonth) -> {
                int monthFormat = monthOfYear + 1;
                String monthOfYearString;
                if (monthFormat < 10) {
                    monthOfYearString = "0" + monthFormat;
                } else {
                    monthOfYearString = "" + monthFormat;
                }

                String dayOfMonthString;
                if (dayOfMonth < 10) {
                    dayOfMonthString = "0" + dayOfMonth;
                } else {
                    dayOfMonthString = "" + dayOfMonth;
                }

                String dateFinal = dayOfMonthString + "/" + monthOfYearString + "/" + year;
                binding.tvIdentityCardBirthDay.setText(dateFinal);
                cardInfoKycDto.getData().getCardData().setDob(dateFinal);

            }, calendarBirth.get(Calendar.YEAR), calendarBirth.get(Calendar.MONTH), calendarBirth.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();
        });

        // back
        binding.llHeader.btnBackHeader.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });

        // CMT
        binding.tvIdentityCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s == null) {
                        return;
                    }

                    if (cardInfoKycDto == null) {
                        cardInfoKycDto = new CardInfoKycDto();
                    }

                    cardInfoKycDto.getData().getCardData().setId(s.toString().trim());
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });

        // tên
        binding.tvIdentityCardFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s == null) {
                        return;
                    }

                    if (cardInfoKycDto == null) {
                        cardInfoKycDto = new CardInfoKycDto();
                    }

                    cardInfoKycDto.getData().getCardData().setName(s.toString().trim());
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });

        // quê quán
        binding.tvIdentityCardHomeTown.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s == null) {
                        return;
                    }

                    if (cardInfoKycDto == null) {
                        cardInfoKycDto = new CardInfoKycDto();
                    }

                    cardInfoKycDto.getData().getCardData().setHometown(s.toString().trim());
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });

        // địa chỉ
        binding.tvIdentityCardAddressDetail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s == null) {
                        return;
                    }

                    if (cardInfoKycDto == null) {
                        cardInfoKycDto = new CardInfoKycDto();
                    }

                    cardInfoKycDto.getData().getCardData().setAddress(s.toString().trim());
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });

        // địa chỉ
        binding.tvIdentityCardNation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s == null) {
                        return;
                    }

                    if (cardInfoKycDto == null) {
                        cardInfoKycDto = new CardInfoKycDto();
                    }

                    cardInfoKycDto.getData().getCardData().setNationality(s.toString().trim());
                } catch (Exception e) {
                    LogUtils.e(e.getMessage());
                }
            }
        });

        // giới tính
        binding.radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (cardInfoKycDto == null) {
                return;
            }

            if (cardInfoKycDto.getData() == null) {
                return;
            }

            if (cardInfoKycDto.getData().getCardData() == null) {
                return;
            }

            if (checkedId == R.id.radio_gender_male) {
                cardInfoKycDto.getData().getCardData().setGender("Name");
            } else if (checkedId == R.id.radio_gender_female) {
                cardInfoKycDto.getData().getCardData().setGender("Nữ");
            }
        });

        // xác nhận
        binding.btnConfirm.setOnClickListener(new OnSingleClickListener() {
            @Override
            public void onSingleClick(View v) {
                hideKeyBroad();

                if (cardInfoKycDto == null) {
                    return;
                }

                if (cardInfoKycDto.getData() == null) {
                    return;
                }

                if (cardInfoKycDto.getData().getCardData() == null) {
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getEkycId())) {
                    showErrorMessage("Confirm/ ekyc_id null", null);
                    return;
                }

                if (binding.tvIdentityCardNumber.getText() == null || binding.tvIdentityCardDate.getText() == null ||
                        binding.tvIdentityCardOutDate.getText() == null || binding.tvIdentityCardFullName.getText() == null
                        || binding.tvIdentityCardBirthDay.getText() == null || binding.tvIdentityCardHomeTown.getText() == null
                        || binding.tvIdentityCardAddressDetail.getText() == null || binding.tvIdentityCardNation.getText() == null
                ) {
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getId())) {
                    showPopupMessage("Thông báo", "CMND/Căn cước chưa có thông tin", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getDate())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin ngày cấp", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getExpiredDate())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin ngày hết hạn", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getName())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin họ tên", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getDob())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin ngày sinh", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getHometown())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin quê quán", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getAddress())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin địa chỉ", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getNationality())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin quốc tịch", null);
                    return;
                }

                if (TextUtils.isEmpty(cardInfoKycDto.getData().getCardData().getGender())) {
                    showPopupMessage("Thông báo", "Chưa có thông tin giới tính", null);
                    return;
                }

                showLoadingView();
                EKycCardIdService.confirmCardData(cardInfoKycDto.getData().getEkycId(), cardInfoKycDto.getData().getCardData(),
                        new OnLoadDataCompleted<BaseRemoteObject>() {
                            @Override
                            public void onFinish(BaseRemoteObject result) {
                                hideLoadingView();

                                final Bundle bundle = new Bundle();
                                bundle.putString(KEY_PHONE, phoneNumber);
                                bundle.putString(KEY_EKYC_ID, cardInfoKycDto.getData().getEkycId());

                                if (result.isSuccess()) {
                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame,
                                            new KycCardStep3Fragment(), bundle, true);
                                } else {
                                    showErrorMessage(result.getMessage(), null);

                                    FragmentUtils.pushFragment(getActivity(), R.id.host_frame,
                                            new KycCardStep3Fragment(), bundle, true);
                                }
                            }

                            @Override
                            public void onError(int errorCode, String message) {
                                hideLoadingView();
                                showErrorMessage(message, null);
                            }
                        });
            }
        });
    }

    private void updateUIProfileID(CardInfoKycDto result) {
        try { // catch tránh trường hợp bị null
            // CMT
            binding.tvIdentityCardNumber.setText(result.getData().getCardData().getId());

            // ngày cấp
            binding.tvIdentityCardDate.setText(result.getData().getCardData().getDate() == null ?
                    "Không có" : result.getData().getCardData().getDate());

            // ngày hết hạn
            binding.tvIdentityCardOutDate.setText(result.getData().getCardData().getExpiredDate() == null ?
                    "Không có" : result.getData().getCardData().getExpiredDate());

            // họ tên
            binding.tvIdentityCardFullName.setText(result.getData().getCardData().getName());

            // ngày sinh
            binding.tvIdentityCardBirthDay.setText(result.getData().getCardData().getDob() == null ?
                    "Không có" : result.getData().getCardData().getDob());

            // quê quán
            binding.tvIdentityCardHomeTown.setText(result.getData().getCardData().getHometown());

            // địa chỉ
            binding.tvIdentityCardAddressDetail.setText(result.getData().getCardData().getAddress());

            // quốc tịch
            binding.tvIdentityCardNation.setText(result.getData().getCardData().getNationality());

            // giới tính
            if (result.getData().getCardData().getGender().toLowerCase().equals("nam")) {
                binding.radioGenderMale.setChecked(true);
            } else {
                binding.radioGenderFemale.setChecked(true);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }
}
