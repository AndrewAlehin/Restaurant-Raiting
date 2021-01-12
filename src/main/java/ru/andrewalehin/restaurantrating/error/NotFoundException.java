package ru.andrewalehin.restaurantrating.error;

public class NotFoundException extends IllegalRequestDataException {

  public NotFoundException(String msg) {
    super(msg);
  }
}