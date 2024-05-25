// dateValidation.test.js

// Import the function to test
import { enableEndDateValidation } from "main/components/Courses/dateValidation";

describe('enableEndDateValidation', () => {
    let startDateInput;
    let endDateInput;
  
    beforeEach(() => {
      // Create mock DOM elements
      startDateInput = document.createElement('input');
      endDateInput = document.createElement('input');
      startDateInput.id = 'startDate';
      endDateInput.id = 'endDate';
      document.body.appendChild(startDateInput);
      document.body.appendChild(endDateInput);
  
      // Call enableEndDateValidation
      enableEndDateValidation();
    });
  
    afterEach(() => {
      // Remove mock DOM elements
      startDateInput.remove();
      endDateInput.remove();
    });
  
    test('end date input min attribute is updated on start date change', () => {
      // Set start date value
      startDateInput.value = '2024-03-01';
  
      // Trigger change event on start date input
      const changeEvent = new Event('change');
      startDateInput.dispatchEvent(changeEvent);
  
      // Check if end date input min attribute is updated
      expect(endDateInput.min).toBe('2024-03-01');
    });

    test('end date input value remains unchanged if greater than start date value', () => {
      // Set start date value
      startDateInput.value = '2024-03-01';
      // Set end date value greater than start date value
      endDateInput.value = '2024-03-02';
  
      // Trigger change event on start date input
      const changeEvent = new Event('change');
      startDateInput.dispatchEvent(changeEvent);
  
      // Check if end date input value remains unchanged
      expect(endDateInput.value).toBe('2024-03-02');
    });
  });