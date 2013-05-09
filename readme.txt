Brogram Stackelberg Implementation
==================================

Upon game initialisation, the Leader class performs the following:

1. Allocates cache array of Record instances, populating the first 60 days via the Platform#query() method.
2. Splits this first 60 days into a training/testing set of 45 and 15 days respectively.
3. Iterates over all possible window size values < 45, training on each (least squares regression), and records the resulting error for the remaining 15 days of data.
4. Selects the window size that minimises this average error.
5. Each subsequent call to proceedNewDay() performs least squares using this window size, applying an additional forgetting factor (0.95). The method additionally populates the cache array with new data to minimise additional RMI requests.
