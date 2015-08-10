package com.kaushiksamba.festemberapp;

import android.content.SharedPreferences;

public class Utilities
{
    public static String username;
    public static String password;
    public static int amount;
    public static String gender;
    public static String shirtSize;
    public static int status;
    public static SharedPreferences prefs;
    public static String url_auth = "http://c904e5c5.ngrok.io/festember15api/mobile_auth.php";
    public static String url_reg = "http://c904e5c5.ngrok.io/festember15api/mobile_tshirtreg.php";
    public static String url_qr = "http://c904e5c5.ngrok.io/festember15api/mobile_tshirt_qr.php";

    public static void init()
    {
        username = password = shirtSize = gender = null;
        amount = 0;
        status=0;
    }
}
