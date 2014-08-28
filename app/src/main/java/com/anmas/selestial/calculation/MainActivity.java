package com.anmas.selestial.calculation;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

    private String mSignDegree;
    private String mSignMinute;
    private String[] mNorthSouth;
    private String[] mEastWest;

    private EditText mEditTextDegreeDelta, mEditTextMinuteDelta;
    private EditText mEditTextDegreeTm, mEditTextMinuteTm;
    private EditText mEditTextDegreeFic, mEditTextMinuteFic;
    private Spinner mSpinNorthSouthDelta, mSpinEastWestTm, mSpinNorthSouthFic;

    private TextView mTextViewAnswerHc, mTextViewAnswerAc;
    private Button mButCalculate, mButClear;

    private class Input {
        Double delta;
        Double tm;
        Double fic;
        Direction directionDelta, directionTm, directionFic;

        public Input(Double delta, Direction directionDelta, Double tm,
                     Direction directionTm, Double fic, Direction directionFic) {
            this.delta = delta;
            this.tm = tm;
            this.fic = fic;
            this.directionDelta = directionDelta;
            this.directionTm = directionTm;
            this.directionFic = directionFic;
        }
    }

    private class Result {
        String hc;
        String ac;

        public Result(String hc, String ac) {
            this.hc = hc;
            this.ac = ac;
        }

    }

    private class MyTextWatcher implements TextWatcher {
        private View v;
        private Double value;

        MyTextWatcher(View v) {
            this.v = v;
        }

        @Override
        public void afterTextChanged(Editable s) {
            String string = String.valueOf(s);
            try {
                value = Double.valueOf(string);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                value = 0.0d;
            }
            switch (v.getId()) {
                case R.id.fic_degree:
                    if (value > 89.0d)
                        mEditTextDegreeFic.setText("89");
                    break;

                case R.id.tm_degree:
                    if (value > 179.0d)
                        mEditTextDegreeTm.setText("179");
                    break;

                case R.id.delta_degree:
                    if (value > 89.0d)
                        mEditTextDegreeDelta.setText("89");
                    break;

                case R.id.fic_minute:
                    if (value > 59.9d)
                        mEditTextMinuteFic.setText("59.9");
                    break;

                case R.id.tm_minute:
                    if (value > 59.9d)
                        mEditTextMinuteTm.setText("59.9");
                    break;

                case R.id.delta_minute:
                    if (value > 59.9d)
                        mEditTextMinuteDelta.setText("59.9");
                    break;
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.but_calculate:
                    Input input = readInput();
                    Result result = calculate(input);
                    mTextViewAnswerHc.setText(result.hc);
                    mTextViewAnswerAc.setText(result.ac);
                    break;
                case R.id.but_clean:
                    clearAllFields();
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSignDegree = getResources().getString(R.string.sign_degree);
        mSignMinute = getResources().getString(R.string.sign_minute);
        mNorthSouth = getResources().getStringArray(R.array.spin_ns);
        mEastWest = getResources().getStringArray(R.array.spin_ew);

        initViews();
        setListeners();

    }

    private void initViews() {
        mEditTextDegreeDelta = (EditText) findViewById(R.id.delta_degree);
        mEditTextMinuteDelta = (EditText) findViewById(R.id.delta_minute);
        mEditTextDegreeTm = (EditText) findViewById(R.id.tm_degree);
        mEditTextMinuteTm = (EditText) findViewById(R.id.tm_minute);
        mEditTextDegreeFic = (EditText) findViewById(R.id.fic_degree);
        mEditTextMinuteFic = (EditText) findViewById(R.id.fic_minute);

        mSpinNorthSouthDelta = (Spinner) findViewById(R.id.delta_ns);
        mSpinEastWestTm = (Spinner) findViewById(R.id.tm_ew);
        mSpinNorthSouthFic = (Spinner) findViewById(R.id.fic_ns);

        mTextViewAnswerHc = (TextView) findViewById(R.id.hc);
        mTextViewAnswerAc = (TextView) findViewById(R.id.Ac);

        mButCalculate = (Button) findViewById(R.id.but_calculate);
        mButClear = (Button) findViewById(R.id.but_clean);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mNorthSouth);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinNorthSouthDelta.setAdapter(adapter);
        mSpinNorthSouthFic.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, mEastWest);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinEastWestTm.setAdapter(adapter);

    }

    private void setListeners() {

        MyOnClickListener onClickListener = new MyOnClickListener();
        mButCalculate.setOnClickListener(onClickListener);
        mButClear.setOnClickListener(onClickListener);

        mEditTextDegreeDelta.addTextChangedListener(new MyTextWatcher(
                mEditTextDegreeDelta));
        mEditTextDegreeTm.addTextChangedListener(new MyTextWatcher(
                mEditTextDegreeTm));
        mEditTextDegreeFic.addTextChangedListener(new MyTextWatcher(
                mEditTextDegreeFic));
        mEditTextMinuteDelta.addTextChangedListener(new MyTextWatcher(
                mEditTextMinuteDelta));
        mEditTextMinuteTm.addTextChangedListener(new MyTextWatcher(
                mEditTextMinuteTm));
        mEditTextMinuteFic.addTextChangedListener(new MyTextWatcher(
                mEditTextMinuteFic));
    }

    private Input readInput() {
        Double delta, tm, fic;
        try {
            double delta_degree = Double.parseDouble(mEditTextDegreeDelta
                    .getText().toString());
            double delta_minute = Double.parseDouble(mEditTextMinuteDelta
                    .getText().toString());
            double tm_degree = Double.parseDouble(mEditTextDegreeTm.getText()
                    .toString());
            double tm_minute = Double.parseDouble(mEditTextMinuteTm.getText()
                    .toString());
            double fic_degree = Double.parseDouble(mEditTextDegreeFic.getText()
                    .toString());
            double fic_minute = Double.parseDouble(mEditTextMinuteFic.getText()
                    .toString());

            delta = delta_degree + delta_minute / 60.0d;
            tm = tm_degree + tm_minute / 60.0d;
            fic = fic_degree + fic_minute / 60.0d;

        } catch (NumberFormatException e) {
            Toast.makeText(this, getResources().getString(R.string.err_input),
                    Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            delta = 0.0d;
            tm = 0.0d;
            fic = 0.0d;
        }

        Direction directionDelta = readSpinners(mSpinNorthSouthDelta);
        Direction directionTm = readSpinners(mSpinEastWestTm);
        Direction directionFic = readSpinners(mSpinNorthSouthFic);

        if (directionDelta == Direction.SOUTH)
            delta *= -1.0d;

        if (directionTm == Direction.WEST)
            tm *= -1.0d;

        if (directionFic == Direction.SOUTH)
            fic *= -1.0d;

        return new Input(delta, directionDelta, tm, directionTm, fic,
                directionFic);
    }

    private Direction readSpinners(Spinner v) {
        int selected = v.getSelectedItemPosition();
        switch (selected) {
            case 0:
                if ((v.getId() == R.id.fic_ns) | (v.getId() == R.id.delta_ns))
                    return Direction.NORTH;
                if (v.getId() == R.id.tm_ew)
                    return Direction.EAST;
                break;
            case 1:
                if ((v.getId() == R.id.fic_ns) | (v.getId() == R.id.delta_ns))
                    return Direction.SOUTH;
                if (v.getId() == R.id.tm_ew)
                    return Direction.WEST;
                break;

        }
        return null;
    }

    private Result calculate(Input inputData) {
        Double delta = Math.toRadians(inputData.delta);
        Double tm = Math.toRadians(inputData.tm);
        Double fic = Math.toRadians(inputData.fic);
        Double hc, ac;
        String result_hc, result_ac;

        hc = Math.asin(Math.sin(fic) * Math.sin(delta) + Math.cos(fic)
                * Math.cos(delta) * Math.cos(tm));

        ac = Math.toDegrees(Math.acos((Math.sin(delta) - Math.sin(fic)
                * Math.sin(hc))
                / (Math.cos(hc) * Math.cos(fic))));

        hc = Math.toDegrees(hc);

        result_hc = formatHc(hc);
        result_ac = formatAc(ac, inputData);

        return new Result(result_hc, result_ac);
    }

    private String formatHc(Double hc) {
        return String.format("%2.0f", Math.floor(hc)) + mSignDegree + " "
                + String.format("%.1f", (hc - Math.floor(hc)) * 60)
                + mSignMinute;
    }

    private String formatAc(Double ac, Input inputData) {
        String tempAc = String.format("%2.0f", Math.floor(ac)) + mSignDegree
                + " " + String.format("%.1f", (ac - Math.floor(ac)) * 60)
                + mSignMinute;

        tempAc += " N";

        if (inputData.directionTm == Direction.WEST)
            tempAc += "W";
        else
            tempAc += "E";
        return tempAc;
    }

    private void clearAllFields() {
        mEditTextDegreeDelta.setText("");
        mEditTextMinuteDelta.setText("");
        mEditTextDegreeTm.setText("");
        mEditTextMinuteTm.setText("");
        mEditTextDegreeFic.setText("");
        mEditTextMinuteFic.setText("");
        mTextViewAnswerHc.setText("");
        mTextViewAnswerAc.setText("");
    }
}
