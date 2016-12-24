package com.carcalendar.dmdev.carcalendar;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Calendar;

import model.Stickers.AnnualVignette;
import model.Stickers.IVignette;
import model.Stickers.MonthVignette;
import model.Stickers.WeekVignette;
import model.Vehicle.Car;

public class AddVehicleCarActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener {
    private Button saveBtn;
    private Button cancelBtn;
    private ImageButton carBtn;
    private Spinner carTypeSpinner;
    private Spinner engineTypeSpinner;
    private Spinner vignetteTypeSpinner;
    private EditText yearText;
    private EditText rangeText;
    private EditText brand;
    private EditText model;
    private EditText oilET;
    private EditText taxAmmount;
    private EditText insuranceAmmount;
    private Spinner insuranceTypeSpinner;
    private Car car;
    private String vehicleType;
    private IVignette vignette = null;
    private boolean datePickerActivated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_car);
        vehicleType = getIntent().getStringExtra("Car");
        saveBtn = (Button) findViewById(R.id.btn_car_save);
        cancelBtn = (Button) findViewById(R.id.cancel_car_btn);
        carBtn = (ImageButton) findViewById(R.id.imageButton_car_add);
        carBtn.setImageResource(getIntent().getIntExtra("Car",R.mipmap.motorcycle_black));
        carTypeSpinner = (Spinner) findViewById(R.id.spinner_type_car);
        engineTypeSpinner = (Spinner) findViewById(R.id.spinner_car_engine);
        vignetteTypeSpinner = (Spinner) findViewById(R.id.vignette_type_spinner);
        brand = (EditText) findViewById(R.id.vehicle_brand);
        model = (EditText) findViewById(R.id.vehicle_model);
        oilET = (EditText) findViewById(R.id.oilEditText);
        taxAmmount = (EditText) findViewById(R.id.tax_ammount_ET);
        insuranceAmmount = (EditText) findViewById(R.id.insurance_ammount_ET);
        insuranceTypeSpinner = (Spinner) findViewById(R.id.insurance_spinner);

        car=new Car();


        String [] carType = getResources().getStringArray(R.array.CarTypes);
        populateSpinner(carTypeSpinner,carType);

        String [] engineType = getResources().getStringArray(R.array.EngineTypes);
        populateSpinner(engineTypeSpinner,engineType);

        String [] vignetteType = getResources().getStringArray(R.array.VignetteTypes);
        populateSpinner(vignetteTypeSpinner,vignetteType);

        String [] insuranceType = getResources().getStringArray(R.array.InsurancePeriod);
        populateSpinner(insuranceTypeSpinner,insuranceType);

        yearText = (EditText) findViewById(R.id.yearEText);
        rangeText = (EditText) findViewById(R.id.rangeEText);

        carTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:         // Sedan
                        car.setCarType("Sedan");
                        break;
                    case 1:         // Jeep
                        car.setCarType("Jeep");
                        break;
                    case 2:         // HatchBack
                        car.setCarType("Hatchback");
                        break;
                    case 3:          // Coupe
                        car.setCarType("Coupe");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                car.setCarType("");
            }
        });

        engineTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:         //Gasoline
                        car.setEngineType("Gasoline");
                        break;
                    case 1:         //Diesel
                        car.setEngineType("Diesel");
                        break;
                    case 2:         //Hybrid
                        car.setEngineType("Hybrid");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                car.setEngineType("");
            }
        });

        vignetteTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:         //Weekly
                        vignette = new WeekVignette();
                        break;
                    case 1:         //Monthly
                        vignette = new MonthVignette();
                        break;
                    case 2:         //Annual
                        vignette = new AnnualVignette();
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                vignette = null;
            }
        });

        insuranceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:         // Three Month
                        break;
                    case 1:         // Annual
                        break;


                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!brand.getText().toString().isEmpty()){
                    car.setBrand(brand.getText().toString());
                }
                else{
                    brand.setError("Please input brand !!");
                }
                if(!model.getText().toString().isEmpty()){
                    car.setModel(model.getText().toString());
                }
                else{
                    model.setError("Please input brand !!");
                }
                if(vignette == null){
                    Toast.makeText(getApplicationContext(),"Please set vignette type",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    car.setVignette(vignette);
                }
                if(car.getCarType() == null || car.getCarType().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please set car type",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(car.getEngineType() == null || car.getEngineType().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please set engine type",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!yearText.getText().toString().isEmpty()) {
                    if (Integer.valueOf(yearText.getText().toString()) < 0) {
                        yearText.setError("Please set year bigger or equal to 0");
                        //Toast.makeText(getApplicationContext(), "Please set year bigger or equal to 0", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        car.setProductionYear(Integer.valueOf(yearText.getText().toString()));
                    }
                }
                if(!rangeText.getText().toString().isEmpty() ) {
                    if (Integer.valueOf(rangeText.getText().toString()) < 0) {
                        rangeText.setError("Please set range bigger or equal to 0");
                        //Toast.makeText(getApplicationContext(), "Please set range bigger or equal to 0", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        car.setKmRange(rangeText.getText().toString());
                    }
                }

                if(!datePickerActivated){
                    Toast.makeText(getApplicationContext(),"Please choose vignette start day !!!",Toast.LENGTH_SHORT).show();
                    return;
                }
                car.setImage(R.mipmap.car_add_image);
                Intent data = new Intent();
                data.putExtra("Car object", car);
                car.setBrand("brand" + String.valueOf(car.getId()));
                car.setModel("model" + String.valueOf(car.getId()));


                setResult(GarageActivity.VEHICLE_ADDED_SUCCESSFULLY,data);
                Log.e("calendar",String.valueOf(((WeekVignette) vignette).getStartDateObject().get(Calendar.YEAR)) + " " + ((WeekVignette) vignette).getStartDateObject().get(Calendar.MONTH) + " " + ((WeekVignette) vignette).getStartDateObject().get(Calendar.DAY_OF_MONTH));
                finish();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                car.setEngineType("");
                car.setCarType("");
                car.setKmRange("");
                car.setProductionYear(0);
                car.setVignette(null);
                vignette = null;
                setResult(GarageActivity.VEHICLE_ADDED_UNSUCCESSFULLY);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        cancelBtn.callOnClick();
    }

    private void populateSpinner(Spinner spinner, String [] arr){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,arr);
        spinner.setAdapter(adapter);
    }

    public void selectDate(View view){
        switch (view.getId()){
            case R.id.date_btn :
                DatePickerFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getSupportFragmentManager(),"datePick");
                datePickerActivated = true;
                return;
            case R.id.btn_next_payment:
                return;
            case R.id.btn_insurance_start :
                return;

            default:
                return;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        //TODO date verification and add in classes
        if(vignette instanceof WeekVignette){
            ((WeekVignette) vignette).setStartDate(year,month,day);
        }
        if(vignette instanceof  MonthVignette){
            ((MonthVignette) vignette).setStartDate(year,month,day);
        }
        if(vignette instanceof  AnnualVignette){
            ((AnnualVignette) vignette).setStartDate(year,month,day);
        }
    }

}

// // TODO: check if all input is handled, change item view in recycler view and then check sorting 
// // TODO: For Dimcho : Please extract recyclerView handling inside GarageActivity, if you have time ofcourse 
