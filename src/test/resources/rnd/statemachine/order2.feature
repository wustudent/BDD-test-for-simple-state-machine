Feature: Order2
    Tests the behavior of the order
# use hash to comment

  Background:
    Given the service is running on localhost with port 8080
    And browser is ready to use

  Scenario: Happy path 2
    When user submit the order
    Then the order is created with a given orderId
    When user pays 2.2 Euros for the order
    Then order completed successfullly
