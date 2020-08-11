package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.openclassrooms.realestatemanager.utils.ConstKt.DOLLAR;
import static com.openclassrooms.realestatemanager.utils.ConstKt.ERROR_GEOCODER_ADDRESS;

/**
 * Created by Philippe on 21/02/2018.
 */

public class Utils {

    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars){
        return (int) Math.round(dollars * 0.812);
    }

    /**
     * Conversion d'un prix dans le type Double (Dollars vers Euros) //TODO : Ok pour double ?
     * @param dollars
     * @return
     */
    public static Double convertDollarToEuroDouble(double dollars)
    {
        return (double) Math.round(dollars * 0.812);
    }



    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param euros
     * @return
     */
    public static int convertEuroToDollar(int euros){
        return (int) Math.round(euros / 0.812);
    }

    /**
     *  Conversion d'un prix dans le type Double (Euros vers Dollars)
     * @param euros
     * @return
     */
    public static Double convertEuroToDollarDouble(double euros)
    {
        return (double) Math.round(euros / 0.812);
    }

    public static String getPriceString (String currency, double price)
    {
        if (currency.equals(DOLLAR))
        {
            return "" + price + "$";
        }
        else
        {
            double euroPrice = convertDollarToEuroDouble(price);
            return "" + euroPrice + "€";
        }
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(new Date());
    }

    public static String getTodayDateGood(Date date){
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(date);
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        WifiManager wifi = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        return wifi.isWifiEnabled();
    }

    public static Boolean isInternetAvailableGood(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();

        /*connectivityManager.isActiveNetworkMetered();
        connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();*/
    }

    public static String geocoderAddress(String address, Context context)
    {
        Geocoder geocoder = new Geocoder(context);
        try {
            List<Address> addressList= geocoder.getFromLocationName(address.toString(),1);
            double lat = addressList.get(0).getLatitude();
            double lng = addressList.get(0).getLongitude();
            return "" + lat + "," + lng;
        } catch (IOException e) {
            e.printStackTrace();
            return ERROR_GEOCODER_ADDRESS;
        }

        /*val geocoder : Geocoder = Geocoder(context)
        val listGeocoder  = geocoder.getFromLocationName(housing.address.toString(), 1)
        val lat  = listGeocoder[0].latitude
        val lng = listGeocoder[0].longitude
        val location = "$lat,$lng"*/
    }
}
