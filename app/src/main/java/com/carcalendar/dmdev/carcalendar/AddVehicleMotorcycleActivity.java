package com.carcalendar.dmdev.carcalendar;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.carcalendar.dmdev.carcalendar.dialogs.DatePickerFragment;
import com.carcalendar.dmdev.carcalendar.utils.DatabaseManager;

import java.io.File;
import java.io.InputStream;
import java.util.Calendar;

import model.Stickers.Insurance;
import model.UserManager;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;
import model.taxes.VehicleTax;
import model.util.ImageUtils;

public class AddVehicleMotorcycleActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener,DatePickerFragment.cancelDate {

    private Button saveBtn;
    private Button cancelBtn;
    private ImageButton motBtn;
    private String pathToImage;
    private Spinner motorcycleTypeSpinner;
    private Spinner engineTypeSpinner;
    private EditText yearText;
    private EditText rangeText;
    private EditText brand;
    private EditText model;
    private EditText oilET;
    private EditText taxAmount;
    private EditText insuranceAmmount;
    private EditText registrationNumber;
    private Spinner insuranceTypeSpinner;
    private Motorcycle motorcycle;
    private boolean taxDatePickerActivated = false;
    private boolean inEditMode = false;
    private UserManager manager = UserManager.getInstance();

    private Uri photoURIFromCamera;
    private Bitmap cameraBitmap;
    private Bitmap galleryBitmap;

    private static final int REQUEST_IMAGE_CAMERA = 0;
    private static final int REQUEST_IMAGE_GALLERY = 1;

    private Motorcycle copyMotorcycle(Motorcycle motorcycleToCpy) {
        Motorcycle motorcycle = new Motorcycle();

        motorcycle.setId(motorcycleToCpy.getId());
        motorcycle.setRegistrationPlate(motorcycleToCpy.getRegistrationPlate());
        motorcycle.setMotorcycleType(motorcycleToCpy.getMotorcycleType());
        motorcycle.setEngineType(motorcycleToCpy.getEngineType());
        motorcycle.setKmRange(motorcycleToCpy.getKmRange());
        motorcycle.setBrand(motorcycleToCpy.getBrand());
        motorcycle.setInsurance(motorcycleToCpy.getInsurance());
        motorcycle.setModel(motorcycleToCpy.getModel());
        motorcycle.setNextOilChange(motorcycleToCpy.getNextOilChange());
        motorcycle.setPathToImage(motorcycleToCpy.getPathToImage());
        motorcycle.setProductionYear(motorcycleToCpy.getProductionYear());
        motorcycle.setTax((VehicleTax) motorcycleToCpy.getTax());

        return motorcycle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle_motorcycle);

        saveBtn = (Button) findViewById(R.id.btn_motor_save);
        cancelBtn = (Button) findViewById(R.id.cancel_motor_btn);
        motBtn = (ImageButton) findViewById(R.id.imageButton_motor_add);
        motBtn.setImageResource(getIntent().getIntExtra("Motor", R.mipmap.motorcycle_black));
        motorcycleTypeSpinner = (Spinner) findViewById(R.id.spinner_type_motor);
        engineTypeSpinner = (Spinner) findViewById(R.id.spinner_motor_engine);
        brand = (EditText) findViewById(R.id.vehicle_brand);
        model = (EditText) findViewById(R.id.vehicle_model);
        oilET = (EditText) findViewById(R.id.oilEditText);
        registrationNumber = (EditText) findViewById(R.id.txt_licence_plate);
        taxAmount = (EditText) findViewById(R.id.tax_ammount_ET);
        insuranceAmmount = (EditText) findViewById(R.id.insurance_ammount_ET);
        insuranceTypeSpinner = (Spinner) findViewById(R.id.insurance_spinner);

        yearText = (EditText) findViewById(R.id.yearEText);
        rangeText = (EditText) findViewById(R.id.rangeEText);

        motorcycle = new Motorcycle();

        if (savedInstanceState != null) {

            if (savedInstanceState.get("imagePath") != null) {
                pathToImage = (String) savedInstanceState.get("imagePath");
                motorcycle.setPathToImage(pathToImage);
            }
            if (savedInstanceState.get("camera") != null) {
                this.cameraBitmap = (Bitmap) savedInstanceState.get("camera");
                motBtn.setImageBitmap(cameraBitmap);
            } else if (savedInstanceState.get("gallery") != null) {
                this.galleryBitmap = (Bitmap) savedInstanceState.get("gallery");
                motBtn.setImageBitmap(galleryBitmap);
            } else {
                motBtn.setImageResource(getIntent().getIntExtra("Car", R.mipmap.car_add_image));
                cameraBitmap = null;
                galleryBitmap = null;
            }
            taxDatePickerActivated = (boolean) savedInstanceState.get("TaxDateBoolean");
            motBtn.refreshDrawableState();
        }

        // Gets the data from an already registered motorcycle
        // Sets the the data fields using the extra Car object
        final Intent launchingIntent = getIntent();
        if (launchingIntent.hasExtra("Car object")) {
            inEditMode = true;

            registrationNumber.setEnabled(false);

            // Copy data from original reference so manager.removeVehicle() works properly
            motorcycle = copyMotorcycle((Motorcycle) launchingIntent.getSerializableExtra("Car object"));

            // Caching old value in in order to use it in an update query
            motorcycle.setRegistrationPlateCache(motorcycle.getRegistrationPlate());

            if (motorcycle.getPathToImage() != null) {
                Bitmap motImage = ImageUtils.getBitmapFromPath(motorcycle.getPathToImage());
                motBtn.setImageBitmap(motImage);
                cameraBitmap = motImage;
            }
            else motBtn.setImageResource(R.mipmap.motorcycle_black);



            // Sets the motorcycle type for ex. : Cruiser, Standard ...
            switch (motorcycle.getMotorcycleType()) {
                case "Cruiser":
                    motorcycleTypeSpinner.setSelection(0);
                    break;
                case "Standard":
                    motorcycleTypeSpinner.setSelection(1);
                    break;
                case "Sportsbike":
                    motorcycleTypeSpinner.setSelection(2);
                    break;
                case "Touring":
                    motorcycleTypeSpinner.setSelection(3);
            }

            // Sets engine type
            switch (motorcycle.getEngineType()) {
                case "Gasoline":
                    engineTypeSpinner.setSelection(0);
                    break;
                case "Diesel":
                    engineTypeSpinner.setSelection(1);
                    break;
                case "Hybrid":
                    engineTypeSpinner.setSelection(2);
            }

            // Sets the insurance type
            switch (motorcycle.getInsurance().getTypeCount()) {
                case 1:
                    insuranceTypeSpinner.setSelection(3);
                    break;
                case 2:
                    insuranceTypeSpinner.setSelection(2);
                    break;
                case 3:
                    insuranceTypeSpinner.setSelection(1);
                    break;
                case 4:
                    insuranceTypeSpinner.setSelection(0);
                    break;
            }

            // Sets the vehicle brand
            brand.setText(motorcycle.getBrand());

            // Sets the motorcycle model
            model.setText(motorcycle.getModel());

            // Sets the tax amount
            taxAmount.setText(String.valueOf(motorcycle.getTax().getAmount()));

            // Sets the production year
            yearText.setText(String.valueOf(motorcycle.getProductionYear()));

            // Sets the range
            rangeText.setText(motorcycle.getKmRange());

            // Sets the registrationPlate
            registrationNumber.setText(motorcycle.getRegistrationPlate());

            // Sets the next Oil change
            oilET.setText(motorcycle.getNextOilChange());

            // Sets the insurance price
            insuranceAmmount.setText(String.valueOf(motorcycle.getInsurance().getPrice()));

            if (motorcycle.getTax().getEndDateAsCalendarObject().get(Calendar.YEAR) > 0){
                taxDatePickerActivated = true;
            }

            pathToImage = motorcycle.getPathToImage();

        } else {
            // Initialize an empty motorcycle object
            motorcycle = new Motorcycle();
            pathToImage = null;
            photoURIFromCamera = null;
            cameraBitmap = null;
            saveBtn.setClickable(false);
        }


        motorcycleTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        motorcycle.setMotorcycleType("Cruiser");
                        break;
                    case 1:
                        motorcycle.setMotorcycleType("Standard");
                        break;
                    case 2:
                        motorcycle.setMotorcycleType("Sportsbike");
                        break;
                    case 3:
                        motorcycle.setMotorcycleType("Touring");

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                motorcycle.setMotorcycleType("");
            }
        });

        engineTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:         //Gasoline
                        motorcycle.setEngineType("Gasoline");
                        break;
                    case 1:         //Diesel
                        motorcycle.setEngineType("Diesel");
                        break;
                    case 2:         //Hybrid
                        motorcycle.setEngineType("Hybrid");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                motorcycle.setEngineType("");
            }
        });

        insuranceTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:         // FOUR
                        motorcycle.getInsurance().setTypeCount(Insurance.Payments.FOUR);
                        break;
                    case 1:         // THREE
                        motorcycle.getInsurance().setTypeCount(Insurance.Payments.THREE);
                        break;
                    case 2:         // TWO
                        motorcycle.getInsurance().setTypeCount(Insurance.Payments.TWO);
                        break;
                    case 3:
                        motorcycle.getInsurance().setTypeCount(Insurance.Payments.ONE);
                        break;
                }
            }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            motorcycle.getInsurance().setTypeCount(null);
        }
    });

        motBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Upload image");
                builder.setMessage("Choose image from");
                builder.setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                File directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                                photoFile = ImageUtils.createImageFile(directory.toString());
                            } catch (Exception ex) {
                                System.err.println("Something went wrong with creating file for image");
                                ex.printStackTrace();
                            }
                            if (photoFile != null) {
                                pathToImage = photoFile.getAbsolutePath();
                                photoURIFromCamera = FileProvider.getUriForFile(getApplicationContext(),
                                        "com.carcalendar.dmdev.carcalendar.fileprovider",photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURIFromCamera);
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAMERA);
                            }
                        }
                    }
                });
                /**
                 * This way is better because we enforce only images to be chosen, also there is no need to provide permission
                 * @see <a href="http://codetheory.in/android-pick-select-image-from-gallery-with-intents/">Gallery intent options</a>
                 */
                builder.setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent();
                        // Show only images, no videos or anything else
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        // Always show the chooser (if there are multiple options available)
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_GALLERY);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(inEditMode){
//                    manager.removeVehicle((Vehicle) launchingIntent.getSerializableExtra("Car object"),false);
//                }

                if (!registrationNumber.getText().toString().isEmpty()) {
                    motorcycle.setRegistrationPlate(registrationNumber.getText().toString());
                } else {
                    registrationNumber.setError("Please enter a registration number!");
                    registrationNumber.requestFocus();
                }
                if (!brand.getText().toString().isEmpty()) {
                    motorcycle.setBrand(brand.getText().toString());
                } else {
                    brand.setError("Please input brand !!");
                    brand.requestFocus();
                }
                if (!model.getText().toString().isEmpty()) {
                    motorcycle.setModel(model.getText().toString());
                } else {
                    model.setError("Please input model !!");
                    model.requestFocus();
                }
                if (motorcycle.getMotorcycleType() == null || motorcycle.getMotorcycleType().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please set motorcycle type", Toast.LENGTH_SHORT).show();
                    motorcycleTypeSpinner.requestFocus();
                    return;
                }
                if (motorcycle.getEngineType() == null || motorcycle.getEngineType().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please set engine type", Toast.LENGTH_SHORT).show();
                    engineTypeSpinner.requestFocus();
                    return;
                }
                if (!yearText.getText().toString().isEmpty()) {
                    if (Integer.valueOf(yearText.getText().toString()) < 0) {
                        yearText.setError("Please set year bigger or equal to 0");
                        yearText.requestFocus();
                        return;
                    } else {
                        motorcycle.setProductionYear(Integer.valueOf(yearText.getText().toString()));
                    }
                }
                if (!rangeText.getText().toString().isEmpty()) {
                    if (Integer.valueOf(rangeText.getText().toString()) < 0) {
                        rangeText.setError("Please set range bigger or equal to 0");
                        rangeText.requestFocus();
                        return;
                    } else {
                        motorcycle.setKmRange(rangeText.getText().toString());
                    }
                }

                if (!taxAmount.getText().toString().isEmpty()) {
                    motorcycle.setTax(Double.valueOf(taxAmount.getText().toString()));
                } else {
                    taxAmount.setError("Please input tax amount");
                    taxAmount.requestFocus();
                    return;
                }

                if (!taxDatePickerActivated) {
                    Toast.makeText(getApplicationContext(), "Please choose date for next tax payment!!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (motorcycle.getInsurance().getTypeForDB() == null){
                    Toast.makeText(view.getContext(),"Please choose insurance period !",Toast.LENGTH_SHORT).show();
                    insuranceTypeSpinner.requestFocus();
                }

                if (insuranceAmmount.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(),"Please enter insurance amount !",Toast.LENGTH_SHORT).show();
                    insuranceAmmount.requestFocus();
                }
                else {
                    motorcycle.getInsurance().setPrice(Double.parseDouble(insuranceAmmount.getText().toString()));
                }

                if (!oilET.getText().toString().isEmpty()){
                    motorcycle.setNextOilChange(oilET.getText().toString());
                }

                if (cameraBitmap != null){
                    ImageUtils.mapImageToVehicle(motorcycle, cameraBitmap);
                }
//                else {
//                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.motorcycle_black);
//                    File picsDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//                    try {
//                        String resourcePath = ImageUtils.saveBitmapImage(picsDir.getAbsolutePath(),bm);
//                        motorcycle.setPathToImage(resourcePath);
//                    } catch (Exception e) {
//                        System.err.println("Problem in saving resource bitmap");
//                        e.printStackTrace();
//                    }
//                    ImageUtils.mapImageToVehicle(motorcycle,bm);
//                }

                DatabaseManager databaseManager = new DatabaseManager(getApplicationContext());
                try {
                    if(inEditMode){
                        if(databaseManager.insert(motorcycle, true) == -3) {
                            Toast.makeText(saveBtn.getContext(), "Vehicle not updated !", Toast.LENGTH_SHORT).show();
                        }
                        manager.removeVehicle((Vehicle) launchingIntent.getSerializableExtra("Car object"), false);

                    } else {
                        manager.addVehicleForDB(motorcycle);
                        //UserManager.saveDataUserManager(view.getContext(),manager);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    setResult(GarageActivity.SOMETHING_WENT_WRONG);
                    finish();
                }

                manager.addVehicle(motorcycle);
                //UserManager.saveDataUserManager(view.getContext(),manager);
                setResult(GarageActivity.VEHICLE_ADDED_SUCCESSFULLY);
                //Log.e("calendar",String.valueOf(((AnnualVignette) vignette).getEndDateObject().get(Calendar.YEAR)) + " " + ((AnnualVignette) vignette).getEndDateObject().get(Calendar.MONTH) + " " + ((AnnualVignette) vignette).getEndDateObject().get(Calendar.DAY_OF_MONTH));
                finish();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = motorcycle.getPathToImage();
                if (!inEditMode && path != null && !path.isEmpty()) {
                    new File(motorcycle.getPathToImage()).delete();

                    motorcycle.setEngineType(null);
                    motorcycle.setMotorcycleType(null);
                    motorcycle.setKmRange(null);
                    motorcycle.setProductionYear(0);
                    motorcycle.setPathToImage(null);
                }

                setResult(GarageActivity.VEHICLE_ADD_CANCELED);
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        cancelBtn.callOnClick();
    }

//    private void populateSpinner(Spinner spinner, String[] arr) {
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, arr);
//        spinner.setAdapter(adapter);
//    }

    public void selectDate(View view) {
        switch (view.getId()) {
            case R.id.btn_next_payment:
                DatePickerFragment taxDateDialog = new DatePickerFragment();
                taxDateDialog.setTag("taxDatePick");
                taxDateDialog.show(getSupportFragmentManager(), "taxDatePick");
                taxDatePickerActivated = true;
                return;
            case R.id.btn_insurance_start:
                DatePickerFragment insuranceDateDialog = new DatePickerFragment();
                insuranceDateDialog.setTag("insuranceDatePick");
                insuranceDateDialog.show(getSupportFragmentManager(), "taxDatePick");
                return;

            default:
                return;
        }

    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

        String tag = (String) datePicker.getTag();
        switch (tag){
            case "taxDatePick":
                motorcycle.getTax().setEndDate(year,month,day);
                break;
            case "insuranceDatePick":
                motorcycle.getInsurance().setStartDate(year, month, day);
                break;
        }

    }

    @Override
    public void onCancelDate(DialogInterface dialog) {
        DatePickerDialog realDialog = (DatePickerDialog) dialog;
        String tag = (String)realDialog.getDatePicker().getTag();
        switch (tag){
            case "taxDatePick":
                taxDatePickerActivated = false;
                motorcycle.getTax().setEndDate(-1,-1,-1);
                break;
            case "insuranceDatePick":
                motorcycle.getInsurance().setStartDate(-1,-1,-1);
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.remove("camera");
        outState.remove("gallery");
        super.onSaveInstanceState(outState);
        if (this.cameraBitmap != null) {
            outState.putParcelable("camera", this.cameraBitmap);
        } else if (this.galleryBitmap != null) {
            outState.putParcelable("gallery", this.galleryBitmap);
        }
        if (motorcycle.getPathToImage() != null) {
            outState.putString("imagePath", motorcycle.getPathToImage());
        }
        outState.putBoolean("TaxDateBoolean",taxDatePickerActivated);
        // outState.putSerializable("dateMap", dateHolder);
    }

    @Override
    public void onRestoreInstanceState(Bundle outState) {
        super.onRestoreInstanceState(outState);

        if (outState.get("imagePath") != null) {
            pathToImage = (String) outState.get("imagePath");
            motorcycle.setPathToImage(pathToImage); // This fucking line was missing
        }

        if (outState.get("camera") != null) {
            this.cameraBitmap = (Bitmap) outState.get("camera");
            motBtn.setImageBitmap(cameraBitmap);
        } else if (outState.get("gallery") != null) {
            this.galleryBitmap = (Bitmap) outState.get("gallery");
            motBtn.setImageBitmap(galleryBitmap);
        } else {
            motBtn.setImageResource(getIntent().getIntExtra("Car", R.mipmap.car_add_image));
            cameraBitmap = null;
            galleryBitmap = null;
        }
        taxDatePickerActivated = (boolean)outState.get("TaxDateBoolean");
        motBtn.refreshDrawableState();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null,null);
                this.galleryBitmap = bitmap;
                new AddVehicleMotorcycleActivity.SaveAndLoadImage().execute(bitmap);

            } catch (Exception e) {
                System.err.println("Problem in getting bitmap from gallery");
                e.printStackTrace();
            }
        }else if (requestCode == REQUEST_IMAGE_CAMERA && resultCode == RESULT_OK){
            Bitmap cameraBitmap = ImageUtils.getScaledBitmapFromPath(pathToImage, motBtn.getWidth(), motBtn.getHeight());
            String realPath=null;
            try {
                realPath = ImageUtils.saveBitmapImage(pathToImage,cameraBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.cameraBitmap = cameraBitmap;
            motBtn.setImageBitmap(this.cameraBitmap);
            motorcycle.setPathToImage(realPath);
        }
    }


    private class SaveAndLoadImage extends AsyncTask<Bitmap,Void,Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... voids) {
            File directoryPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            String pathToBitmap = null;
            Bitmap bm = null;
            try {
                pathToBitmap = ImageUtils.saveBitmapImage(directoryPath.getAbsolutePath(),voids[0]);
                motorcycle.setPathToImage(pathToBitmap);
                pathToImage = pathToBitmap;
                bm = BitmapFactory.decodeFile(pathToBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bm;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if(bitmap != null){
                motBtn.setImageBitmap(bitmap);
                motBtn.refreshDrawableState();
                cameraBitmap = bitmap;
                saveBtn.setClickable(true);
            }
        }
    }
}