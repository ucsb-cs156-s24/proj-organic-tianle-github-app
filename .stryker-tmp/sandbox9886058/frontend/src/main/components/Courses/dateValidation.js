export function enableEndDateValidation() {
    const startDateInput = document.getElementById("startDate");
    const endDateInput = document.getElementById("endDate");

    // Function to update the min attribute of the end date input
    const updateEndDateMin = () => {
        endDateInput.min = startDateInput.value;
    };

    // Add event listener to the start date input field
    startDateInput.addEventListener("change", updateEndDateMin);

    // Call updateEndDateMin initially
    updateEndDateMin();
}
