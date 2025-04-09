package edu.aku.hassannaqvi.fitlife.models;

import static edu.aku.hassannaqvi.fitlife.core.MainApp._EMPTY_;
import static edu.aku.hassannaqvi.fitlife.core.UserAuth.generatePassword;

import android.database.Cursor;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import edu.aku.hassannaqvi.fitlife.BR;

public class NewUser extends BaseObservable {
    private String fullName = _EMPTY_;
    private String age= _EMPTY_;
    private String gender= _EMPTY_;
    private String mobileNumber= _EMPTY_;
    private String email= _EMPTY_;
    private String username= _EMPTY_;
    private String password= _EMPTY_;

    public NewUser(String fullName, String age, String gender, String mobileNumber, String email, String username, String password) {
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public NewUser() {
        // Default constructor
    }

    @Bindable
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        notifyPropertyChanged(BR.fullName);
    }

    @Bindable
    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
        notifyPropertyChanged(BR.age);
    }

    @Bindable
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyPropertyChanged(BR.gender);
    }

    @Bindable
    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
        notifyPropertyChanged(BR.mobileNumber);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        notifyPropertyChanged(BR.username);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public void hydrate(Cursor cursor) {
        this.fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"));
        this.age = cursor.getString(cursor.getColumnIndexOrThrow("age"));
        this.gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
        this.mobileNumber = cursor.getString(cursor.getColumnIndexOrThrow("mobileNumber"));
        this.email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
        this.username = cursor.getString(cursor.getColumnIndexOrThrow("username"));
        this.password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
    }

    public JSONObject toJSONObject() throws JSONException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject json = new JSONObject();
        json.put("fullName", fullName);
        json.put("age", age);
        json.put("gender", gender);
        json.put("mobileNumber", mobileNumber);
        json.put("email", email);
        json.put("username", username);
        json.put("password", generatePassword(password, null));
        return json;
    }
}