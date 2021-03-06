package model;

import android.content.Context;
import android.util.Log;

import com.carcalendar.dmdev.carcalendar.utils.DatabaseHelper;
import com.carcalendar.dmdev.carcalendar.utils.DatabaseManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Vehicle.Vehicle;
import model.authentication.IUserAuthenticator;
import model.authentication.UsedUsernameException;

/**
 * Singleton class UserManager for creating, registering and managing users.
 */
public class UserManager implements IUserAuthenticator,Serializable {

    private static final UserManager manager = new UserManager();
    public static final String SAVE_USER_MANAGER = "USER_MANAGER_SAVE";
    public static UserManager getInstance() {
        return manager;
    };
    private TreeSet<User> registeredUsers;
    private static int userId =0;
    private User loggedUser;
    private static DatabaseManager dbManager = null;

    private UserManager() {

        registeredUsers = new TreeSet<User>();
        loggedUser = null;
    }

    /**
     * Sets android context to database manager. Android specific !
     * @param context
     */
    public void setDbContext(Context context){
        dbManager = new DatabaseManager(context);
        Log.d("DB", "DB created!");
    }

    public boolean isDbInitialized() {
        return dbManager != null;
    }

    /**
     * Increments userId and passes it to the created user.
     */
    public User createUser(String name,String password, int age){
        //userId++;
        return new User(name,password,age);
    }

    private boolean isPasswordGood(String password){
        final Pattern passPattern = Pattern.compile( "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        Matcher matcher = passPattern.matcher(password);
        return matcher.matches();
    }

    public void registerUser(User x){
        registeredUsers.add(x);
    }

    public void registerUserForDB(User x){
        try {
            dbManager.insertUser(x.name,x.password,x.age);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addVehicle(Vehicle x){
        loggedUser.addVehicle(x);
    }


    public void addVehicleForDB(Vehicle x) throws Exception{
        dbManager.insert(x,false);
    }

    public void removeVehicle(Vehicle v, boolean removeImageAlso){
        loggedUser.removeVehicle(v,removeImageAlso);
    }

//    TODO : removing only image path from DB - Dimcho
    public void removeVehicleForDB(Vehicle v){
        dbManager.delete(DatabaseHelper.VEHICLES_TABLE, "registration = '" + v.getRegistrationPlate() + "'");
        dbManager.delete(DatabaseHelper.TAXES_TABLE, "vehicle_registration = '" + v.getRegistrationPlate() + "'");
    }

    public String getLoggedUserName() {
       return loggedUser.name;
   }

    public String getLoggedUserNameFromDB() {

        try {
            return dbManager.getLoggedUserFromDB()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @return
     */
    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Gets logged user from DB. For initial load only.
     * @return
     */
    public User getLoggedUserFromDB(){
        try {
            String[] loggedUserData = dbManager.getLoggedUserFromDB();
            if (loggedUserData != null) {
                loggedUser = new User(loggedUserData[0],
                        loggedUserData[1],
                        Integer.parseInt(loggedUserData[2]));
            }
            else return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loggedUser;
    }

    public List<Vehicle> getRegisteredUserVehicles(){
        return new ArrayList<>(loggedUser.ownedVehicles);
    }

    /**
     * Gets all vehicles from all users with full data
     * @return
     */
    public Map<String,List<Vehicle>> getAllVehicles(){

        return dbManager.getVehiclesForAllUsers();

    }

    /**
     * Gets logged user vehicles with included data from DB. For initial load only.
     * @return
     */
    public List<Vehicle> getRegisteredUserVehiclesFromDB(){
        try {
            if (dbManager.getVehiclesForUser(loggedUser.name) == null){
                return null;
            }
            else {
                ArrayList<Vehicle> tmplist = (ArrayList<Vehicle>) dbManager.getVehiclesForUser(loggedUser.name);
                loggedUser.ownedVehicles = new TreeSet<>(tmplist);
                return tmplist;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *  setting "loggedUser" to null !!
     */
    public void userLogout(){
        loggedUser = null;
    }

    public void userLogoutForDB(){
        dbManager.updateUser(loggedUser.name,loggedUser.password,loggedUser.age,true,false);
    }

    /**
     * IF YOU UPDATE USERMANAGER FROM FILE YOU MUST USE ONLY THIS METHOD AS A SOLUTION !!!
     * @param x - UserManager Singleton
     */
//    public void updateFromFile(UserManager x){
//        this.loggedUser = x.loggedUser;
//        this.registeredUsers = x.registeredUsers;
//        userId = x.registeredUsers.last().getId();
//    }

    /**
     * For testing purposes
     * @param username
     */
    public void setLoggedUserUsername(String username) {
        loggedUser = new User(username, "ddd", 22);
    }

    /**
     * @return arraylist with all users username
     */
    public ArrayList<String> getAllRegisteredUsersUsername() {
        ArrayList<String> usernames = new ArrayList<>();
        for (User x : registeredUsers){
            usernames.add(x.name);
        }
        return usernames;
    }

    /**
     * Wraps the user data in a User object and stores it in the registeredUser collection
     *
     * @param name the user's username
     * @param password the user's password
     * @param age the user's age
     */
    public void addToRegisteredUsers(String name, String password, int age){
        registerUser(new User(name,password,age));
    }

    /**
     * Update UserManager and Model from the db
     */
    public void updateFromDB() {
        // Call getLoggedUser() method from db manager
    }

    /**
     *  ANDROID SPECIFIC !!!
     *  Serializing the UserManager object to internal storage  with openFileOutput()
     * @param x - UserManager
     */
//    public static void saveDataUserManager(Context context, final UserManager x){
//        Intent intent = new Intent(context,StorageManager.class);
//        intent.putExtra(SAVE_USER_MANAGER,x);
//        context.startService(intent);
//    }

    /**
     *
     * @param username - String
     * @param password - String
     * @return true if login details are correctly entered, false otherwise
     */
    @Override
    public boolean authenticateLogin(String username, String password)
    {
        int age = dbManager.checkUserLogin(username, password);
        if(age == 0) {
            return false;
        }

        loggedUser = new User(username, password, age);
        dbManager.updateUser(loggedUser.name,loggedUser.password,loggedUser.age,true,true);
        return true;
    }


    /**
     *
     * @param username - String
     * @param password - String
     * @return true if mail and password are good, false otherwise
     */
    @Override
    public boolean validateRegister(String username, String password) throws UsedUsernameException{
        if(registeredUsers.isEmpty()){
            return (!username.isEmpty() && isPasswordGood(password));
        }
        if(!registeredUsers.isEmpty()) {
            if(!username.isEmpty() && isPasswordGood(password)){
                for (User x : registeredUsers) {
                    if (x.name.equals(username)) {
                        throw new UsedUsernameException();
                    }
                }
                return true;
            }
            else return false;
        }
        return false;
    }

    private class User implements Comparable<User>,Serializable {
        private String name;
        private String password;
        private int age;
        private int id;
        private TreeSet<Vehicle> ownedVehicles;

        private User(String name, String password, int age) {
            this.name = name;
            this.age = age;
            this.password = password;
            ownedVehicles = new TreeSet<>();
           // this.id = id;
        }
//        private User(User x){
//            this.name = x.name;
//            this.age = x.age;
//            this.password = x.password;
//            ownedVehicles = x.ownedVehicles;
//            this.id = x.id;
//        }


        public int getId() {
            return id;
        }

         void addVehicle(Vehicle x) {
            if (x != null) {
                ownedVehicles.add(x);
            } else {
                throw new NullPointerException();
            }
        }

        public void removeVehicle(Vehicle x,boolean removeImageAlso) {
            if (ownedVehicles.contains(x)) {
                ownedVehicles.remove(x);
                if (removeImageAlso) {
                    removeVehicleImage(x.getPathToImage());
                }
            }
        }

        public void removeVehicleImage(String pathToImage){
            if(pathToImage != null) {
                File imageofCar = new File(pathToImage);
                if (!imageofCar.isDirectory() && imageofCar.length() > 0) imageofCar.delete();
            }
        }

        @Override
        public int compareTo(User user) {
            // No id's used anymore. Breaks adding two or more users.
//            if(this.id < user.id) return -1;
//            if(this.id == user.id) return 0;
//            return 1;

            if(this.name.equals(user.name)) return 0;
            if(this.name.charAt(0) > name.charAt(0)) return -1;

            return 1;
        }
    }
}
