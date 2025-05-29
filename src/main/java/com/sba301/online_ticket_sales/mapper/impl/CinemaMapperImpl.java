package com.sba301.online_ticket_sales.mapper.impl;

import com.sba301.online_ticket_sales.dto.cinema.request.CinemaRequest;
import com.sba301.online_ticket_sales.dto.cinema.request.RoomRequest;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaDetailResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.CinemaResponse;
import com.sba301.online_ticket_sales.dto.cinema.response.RoomResponse;
import com.sba301.online_ticket_sales.entity.Cinema;
import com.sba301.online_ticket_sales.entity.Room;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.CinemaMapper;
import com.sba301.online_ticket_sales.repository.CinemaRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CinemaMapperImpl implements CinemaMapper {
  private final CinemaRepository cinemaRepository;

  public CinemaMapperImpl(CinemaRepository cinemaRepository) {
    this.cinemaRepository = cinemaRepository;
  }

  @Override
  public Cinema toCinema(CinemaRequest request) {
    if (request.getRequestType().isCreate()) {
      Cinema cinema = new Cinema();
      cinema.setName(request.getName());
      cinema.setAddress(request.getAddress());
      cinema.setHotline(request.getHotline());
      cinema.setProvince(request.getProvince());

      request
          .getRoomRequestList()
          .forEach(
              roomRequest -> {
                Room room = new Room();
                room.setName(roomRequest.getName());
                room.setRoomType(roomRequest.getType());
                cinema.addRoom(room);
              });
      return cinema;
    }

    if (request.getId() == null) {
      throw new AppException(ErrorCode.CINEMA_NOT_FOUND);
    }

    Cinema cinema =
        cinemaRepository
            .findById(request.getId())
            .orElseThrow(() -> new AppException(ErrorCode.CINEMA_NOT_FOUND));

    cinema.setName(request.getName());
    cinema.setAddress(request.getAddress());
    cinema.setHotline(request.getHotline());
    cinema.setProvince(request.getProvince());

    if (request.getRoomRequestList() != null) {
      Map<Long, RoomRequest> roomRequestMap =
          request.getRoomRequestList().stream()
              .filter(r -> r.getId() != null)
              .collect(Collectors.toMap(RoomRequest::getId, Function.identity()));

      List<Room> existingRooms = cinema.getRooms();

      existingRooms.forEach(
          existingRoom -> {
            RoomRequest roomReq = roomRequestMap.get(existingRoom.getId());
            if (roomReq != null) {
              existingRoom.setName(roomReq.getName());
              existingRoom.setRoomType(roomReq.getType());
              roomRequestMap.remove(existingRoom.getId());
            }
          });

      request.getRoomRequestList().stream()
          .filter(r -> r.getId() == null)
          .forEach(
              roomRequest -> {
                Room newRoom = new Room();
                newRoom.setName(roomRequest.getName());
                newRoom.setRoomType(roomRequest.getType());
                cinema.addRoom(newRoom);
              });
    }

    return cinema;
  }

  @Override
  public CinemaResponse toCinemaResponse(Cinema cinema) {
    CinemaResponse response = new CinemaResponse();
    response.setId(cinema.getId());
    response.setName(cinema.getName());
    response.setAddress(cinema.getAddress());
    response.setHotline(cinema.getHotline());
    response.setProvince(cinema.getProvince());
    response.setActive(cinema.isActive());
    return response;
  }

  @Override
  public CinemaDetailResponse toCinemaDetailResponse(Cinema cinema) {
    CinemaDetailResponse response = new CinemaDetailResponse();
    response.setId(cinema.getId());
    response.setName(cinema.getName());
    response.setAddress(cinema.getAddress());
    response.setHotline(cinema.getHotline());
    response.setProvince(cinema.getProvince());
    response.setActive(cinema.isActive());
    response.setRoomResponseList(
        cinema.getRooms().stream()
            .map(
                room -> {
                  RoomResponse roomResponse = new RoomResponse();
                  roomResponse.setId(room.getId());
                  roomResponse.setName(room.getName());
                  roomResponse.setRoomType(room.getRoomType());
                  return roomResponse;
                })
            .toList());
    return response;
  }
}
