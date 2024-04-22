package zerobase.reservation.repository;

/**
 * Allow customers to look up all restaurants
 */

public interface RestaurantsProjection {
    
    Long getId();
    String getName();
    Integer getCapacity();
    String getDescription();
    String getLocation();
    String getPhoneNumber();
}