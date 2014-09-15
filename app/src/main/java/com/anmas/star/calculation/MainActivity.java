package com.anmas.star.calculation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anmas.starcalc.R;

public class MainActivity extends ActionBarActivity {

    private String signDegree;
    private String signMinute;
    private String[] northSouth;
    private String[] eastWest;

    /*Views*/
    private EditText editTextDegreeDelta, editTextMinuteDelta;
    private EditText editTextDegreeTm, editTextMinuteTm;
    private EditText editTextDegreeFic, editTextMinuteFic;
    private Spinner spinNorthSouthDelta, spinEastWestTm, spinNorthSouthFic;
    private TextView textViewAnswerHc, textViewAnswerAc;

    private AdapterView.OnItemSelectedListener onSpinnerItemClickListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            doIt();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signDegree = getResources().getString(R.string.sign_degree);
        signMinute = getResources().getString(R.string.sign_minute);
        northSouth = getResources().getStringArray(R.array.spin_ns);
        eastWest = getResources().getStringArray(R.array.spin_ew);

        initViews();
        setListeners();

        Toast.makeText(this, getResources().getString(R.string.err_empty_field), Toast.LENGTH_SHORT).show();
    }

    private void initViews() {
        editTextDegreeDelta = (EditText) findViewById(R.id.delta_degree);
        editTextMinuteDelta = (EditText) findViewById(R.id.delta_minute);
        editTextDegreeTm = (EditText) findViewById(R.id.tm_degree);
        editTextMinuteTm = (EditText) findViewById(R.id.tm_minute);
        editTextDegreeFic = (EditText) findViewById(R.id.fic_degree);
        editTextMinuteFic = (EditText) findViewById(R.id.fic_minute);

        spinNorthSouthDelta = (Spinner) findViewById(R.id.delta_ns);
        spinEastWestTm = (Spinner) findViewById(R.id.tm_ew);
        spinNorthSouthFic = (Spinner) findViewById(R.id.fic_ns);

        textViewAnswerHc = (TextView) findViewById(R.id.hc);
        textViewAnswerAc = (TextView) findViewById(R.id.Ac);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, northSouth);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNorthSouthDelta.setAdapter(adapter);
        spinNorthSouthDelta.setOnItemSelectedListener(onSpinnerItemClickListener);
        spinNorthSouthFic.setAdapter(adapter);
        spinNorthSouthFic.setOnItemSelectedListener(onSpinnerItemClickListener);

        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, eastWest);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinEastWestTm.setAdapter(adapter);
        spinEastWestTm.setOnItemSelectedListener(onSpinnerItemClickListener);

    }

    private void setListeners() {
        editTextDegreeDelta.addTextChangedListener(new MyTextWatcher(
                editTextDegreeDelta));
        editTextDegreeTm.addTextChangedListener(new MyTextWatcher(
                editTextDegreeTm));
        editTextDegreeFic.addTextChangedListener(new MyTextWatcher(
                editTextDegreeFic));
        editTextMinuteDelta.addTextChangedListener(new MyTextWatcher(
                editTextMinuteDelta));
        editTextMinuteTm.addTextChangedListener(new MyTextWatcher(
                editTextMinuteTm));
        editTextMinuteFic.addTextChangedListener(new MyTextWatcher(
                editTextMinuteFic));
    }

    private void doIt() {
        if (!allTextFieldsAreFilled())
            return;
        Input input = readInput();
        if (input == null)
            return;
        Result result = calculate(input);
        textViewAnswerHc.setText(result.hc);
        textViewAnswerAc.setText(result.ac);
    }

    private Input readInput() {
        Double delta, tm, fic;
        try {
            double delta_degree = Double.parseDouble(editTextDegreeDelta
                    .getText().toString());
            double delta_minute = Double.parseDouble(editTextMinuteDelta
                    .getText().toString());
            double tm_degree = Double.parseDouble(editTextDegreeTm.getText()
                    .toString());
            double tm_minute = Double.parseDouble(editTextMinuteTm.getText()
                    .toString());
            double fic_degree = Double.parseDouble(editTextDegreeFic.getText()
                    .toString());
            double fic_minute = Double.parseDouble(editTextMinuteFic.getText()
                    .toString());

            delta = delta_degree + delta_minute / 60.0d;
            tm = tm_degree + tm_minute / 60.0d;
            fic = fic_degree + fic_minute / 60.0d;

        } catch (NumberFormatException e) {
            if (allTextFieldsAreFilled())
                Toast.makeText(this, getResources().getString(R.string.err_input), Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(this, getResources().getString(R.string.err_empty_field), Toast.LENGTH_SHORT).show();
            clearResults();
            return null;
        }

        Direction directionDelta = readSpinners(spinNorthSouthDelta);
        Direction directionTm = readSpinners(spinEastWestTm);
        Direction directionFic = readSpinners(spinNorthSouthFic);

        if (directionDelta == Direction.SOUTH)
            delta *= -1.0d;

        if (directionTm == Direction.WEST)
            tm *= -1.0d;

        if (directionFic == Direction.SOUTH)
            fic *= -1.0d;

        return new Input(delta, directionDelta, tm, directionTm, fic,
                directionFic);
    }

    private boolean allTextFieldsAreFilled() {
        return isTextViewFilled(editTextDegreeDelta) &&
                isTextViewFilled(editTextMinuteDelta) &&
                isTextViewFilled(editTextDegreeTm) &&
                isTextViewFilled(editTextMinuteTm) &&
                isTextViewFilled(editTextDegreeFic) &&
                isTextViewFilled(editTextMinuteFic);

    }

    private boolean isTextViewFilled(TextView view) {
        return view.getText().toString().length() > 0;
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
        return String.format("%2.0f", Math.floor(hc)) + signDegree + " "
                + String.format("%.1f", (hc - Math.floor(hc)) * 60)
                + signMinute;
    }

    private String formatAc(Double ac, Input inputData) {
        String tempAc = String.format("%2.0f", Math.floor(ac)) + signDegree
                + " " + String.format("%.1f", (ac - Math.floor(ac)) * 60)
                + signMinute;

        tempAc += " N";

        if (inputData.directionTm == Direction.WEST)
            tempAc += "W";
        else
            tempAc += "E";
        return tempAc;
    }

    private void clearAllFields() {
        editTextDegreeDelta.setText("");
        editTextMinuteDelta.setText("");
        editTextDegreeTm.setText("");
        editTextMinuteTm.setText("");
        editTextDegreeFic.setText("");
        editTextMinuteFic.setText("");
        clearResults();
    }

    private void clearResults() {
        textViewAnswerHc.setText("");
        textViewAnswerAc.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.act_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear:
                clearAllFields();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private enum Direction {
        NORTH, SOUTH, EAST, WEST
    }

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
                clearResults();
                return;
            }
            switch (v.getId()) {
                case R.id.fic_degree:
                    if (value > 89.0d)
                        editTextDegreeFic.setText("89");
                    break;

                case R.id.tm_degree:
                    if (value > 179.0d)
                        editTextDegreeTm.setText("179");
                    break;

                case R.id.delta_degree:
                    if (value > 89.0d)
                        editTextDegreeDelta.setText("89");
                    break;

                case R.id.fic_minute:
                    if (value > 59.9d)
                        editTextMinuteFic.setText("59.9");
                    break;

                case R.id.tm_minute:
                    if (value > 59.9d)
                        editTextMinuteTm.setText("59.9");
                    break;

                case R.id.delta_minute:
                    if (value > 59.9d)
                        editTextMinuteDelta.setText("59.9");
                    break;
            }
            doIt();

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
}
