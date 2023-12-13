# mammon_billing
Open source invoicing software written in Java

This project was originally started to familarize myself with Java.
Specifically, I wanted to see how difficult it was to build a relatively reactive interface from scratch (AWT and Swing in this case).

The project consists of several components. I opted for a separation of the interface and the data logic as much as possible. Often, this was done by creating UI-classes. 
The component containing data logic is an extension of its UI-class.

The result is  a program with roughly the following structure:
![alt text](https://www.planetegem.be/demo/mammon_billing/mammon-schema.png)

An SQL-database contains all invoice data saved by the user. Interaction with this database are executed with Apache Derby.
Afterwards, users can create a PDF from their invoice: this is done using Apache PDFBox.
Everything was compiled to a single jar with Maven, the assembly:single goal being used to take dependencies with it.
