package com.sba301.online_ticket_sales.service;

import java.util.Map;

public interface CloudinaryService {
  Map<String, String> uploadImage(byte[] image, String folder);

  boolean deleteImage(String mediaKey);

  String getImageUrl(String assetKey);
}
