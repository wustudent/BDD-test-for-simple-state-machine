Feature: Order
    Tests the behavior of the order

  Background:
    Given the service is running on localhost with port 8080
    And browser is ready to use

  Scenario: Happy path
    When user submit the order
    Then the order is created with a given orderId
    When user pays 2.2 Euros for the order
    Then order completed successfully

  Scenario: Bad path: not enough money
    When user submit the order
    Then the order is created with a given orderId
    When user pays 0.1 Euros for the order
    Then order not completed

  Scenario: Bad path: orderId does not exist
    When user submit the order
    Then the order is created with a given orderId
    When user pays 2.2 Euros for an order does not exist
    Then order failed
