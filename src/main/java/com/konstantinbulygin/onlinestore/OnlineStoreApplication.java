package com.konstantinbulygin.onlinestore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class OnlineStoreApplication {

    public static void main(String[] args) {


//        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
//                "cloud_name", "korustlt",
//                "api_key", "619614386456773",
//                "api_secret", "kAYZ3cfRWBRsBPyX_miIRL0PKEI"));
//
//        Map params = ObjectUtils.asMap(
////                "public_id", "images",
//                "overwrite", true,
//                "notification_url", "https://mysite.com/notify_endpoint",
//                "resource_type", "image"
//        );

//        Map uploadResult = cloudinary.uploader().upload(new File("1.jpeg"), params);
//
//        System.out.println("<<=================== " + uploadResult.get("secure_url") + "<<=================== ");


        SpringApplication.run(OnlineStoreApplication.class, args);
    }

}
