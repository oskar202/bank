package com.bank.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;

@Service
public class ClientCountry {

    public String getUserLocationByIp() {
        try {
            URL whatIsMyIP = new URL("https://ipv4.icanhazip.com/");
            String ip; // When not running in localhost, ip should be taken from: HttpServletRequest getRemoteHost()
            try (BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()))) {
                ip = in.readLine();
            }
            return getLocation(ip);
        } catch (IOException | GeoIp2Exception e) {
            return "Unknown location";
        }
    }

    private String getLocation(String ip) throws IOException, GeoIp2Exception {
        File database = new File("payments/src/main/resources/GeoLite2-Country.mmdb");
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CountryResponse response = dbReader.country(ipAddress);
        return response.getCountry().getName();
    }
}
