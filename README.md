### Adding values of all checkboxes displayed on an UI component, in a single column of a table instead of creating a seperate table and mapping each checkbox as a column
#### `Bitfield` and `Enums` will be used to achieve the above mentioned goal
#### Tech Stack considered
* Java (Spring-boot)
* Typescript
* Relational Database (RDB)
---
Consider a simple UI screen that asks a patient a question during an appointment

**Mark all the symptoms you are experiencing**
- [ ] Fever
- [ ] Cough
- [ ] Headache
- [ ] Cold

Instead of creating a `symptoms` entity with boolean fields matching the checkbox values (`has_fever`, `coughing`,`has_cold` etc) and establishing a One-to-one relationship with the `appointment` entity, We can create a column `symptoms` in the `appointment` table with data type being `int`

The value of the first checkbox value will be set to 1 and the value of remaining checkboxes will be calculated by multipliying the value of previous checkbox by 2

- [ ] Fever -> 1
- [ ] Cough -> 1*2 = 2
- [ ] Headache -> 2*2 = 4
- [ ] Cold -> 4*2 = 8

The values of all the checkboxes that the user has checked will be added and stored in the database, and upon value retreival we'll be able to find out the values that were checked

* 3: Fever and Cough
* 4: Headache
* 9: Fever and Cold

and so on.
* This approach will always produce a unique combination of enum(s) from a numeric value, it is imposible for a numeric value to refer to more than 1 combination
* Only 1 `If statement` is required to know if an enum(checkbox) value is present inside of a numeric value (code snippet provided below or refer this [Class](https://github.com/hardikSinghBehl/checkbox-values-bitfield-enum-poc/blob/main/src/main/java/com/behl/brahma/utility/BitUtil.java))

---
## Sample Screen Recording (1 minute long)

https://user-images.githubusercontent.com/69693621/135916601-6d77169e-8fc8-429d-9473-134440855e0f.mov

---

## Implementation

1.) Create an Enum matching the checkboxes with a getter method to retreive the numeric value associated with the enum
```
public enum Symptom {

    FEVER, COUGH, HEADACHE, EYE_ACHE, COLD;

    public Integer getBitFlagValue() {
        return 1 << this.ordinal();
    }

}
```

2.) Create a utility method
  * to calculate a numeric value from a Set of enums
  ```
  public static Integer getSymptomValue(Set<Symptom> symptoms) {
        final var value = new AtomicInteger(0);
        symptoms.forEach(symptom -> {
            value.addAndGet(symptom.getBitFlagValue());
        });
        return value.get();
    }
  ```
* to convert a numeric value to set of enums
```
  public static EnumSet<Symptom> getSymptoms(final Integer symptomValue) {
  
      EnumSet<Symptom> symptoms = EnumSet.noneOf(Symptom.class);
      Arrays.asList(Symptom.values()).forEach(symptom -> {
          Integer currentIterationSymptomBitValue = symptom.getBitFlagValue();
          if ((currentIterationSymptomBitValue & symptomValue) == currentIterationSymptomBitValue)
              symptoms.add(symptom);
      });
      return symptoms;
  }
```

4.) Create an entity `appointment` with the field `symptom`
```
@Entity
@Table(name = "appointments")
@Data
public class Appointment {

    @Id
    private Integer id;

    @Column(nullable = false)
    private Integer symptoms;

    @Column(nullable = false)
    private Integer prescription;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

}
```
5.) Take Set(s) of created enum types from frontend and map to DTO object

```
public class AppointmentCreationRequestDto {

    private final UUID patientId;
    private final Set<Symptom> symptoms;
    private final Set<Prescription> prescriptions;

}
```
  * JSON Schema
  <img width="920" alt="Screenshot 2021-10-05 at 9 29 17 AM" src="https://user-images.githubusercontent.com/69693621/135958235-a083d54f-cfb0-48d0-bf62-b51ce0b08ee6.png">
  
  * Sample JSON
  ```
{
  "patientId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "symptoms": [
    "COLD", "COUGH"
  ],
  "prescriptions": [
    "KINGFISHER"
  ]
}
  ```
  
  The Recieved JSON containing the Set of enums will automatically be mapped to the DTO class created above by spring-boot, if using another tech-stack, the same can be done explicitely

6.) Use the above created utility method to convert recieved set of enums to a numeric value and save it in the database
```
appointment.setSymptom(BitUtil.getSymptomValue(dto.getSymptoms()));
appointment.saveToDataBase();
```
similarly, convert the numeric value to Set of Enums when sending response to frontend
```
var appointment = findById(receivedIdFromFrontend);
int symptomValue = appointment.getSymptom();
appointmentDto.setSymptoms(BitUtil.getSymptoms(symptomValue));
return appointmentDto;
```
---
## Points to consider
* After deployment, the order in the Enum should not be changed... it'll mess up all the previous records (self-explanatory)
* **Only insertion to the back of enum is allowed**
  * if a checkbox/value is to be removed/updated... insert the new change to the back of enum wihhout touching the field order
* if want to do native sql operations of the various enum values, map them to SET (details mentioned below)
* The approach can be modified according to the use case, if multiple screens are available containing checkboxes and few other input fields, a common entity can be created that can store each module as a column and can be mapped with the main entity (One-to-one, Many-to-one etc)
* You don't take a pill when you dont have a headache, similarly dont implement the said appraoch, if it does not solve the problem, Approach is perfect when a new table is created specifically to store only boolean values corresponding to checkboxes (Creating a new table and mapping 20+ checkboxes as seperate columns is something that should be avoided and mentioned approach can be used to solve this)

---
## Set in MySQL
#### MySQL internally uses similar/extact implementation to store enums/sets respectively
#### [REFERENCE](https://dev.mysql.com/doc/refman/8.0/en/set.html)

##### Should be clear just by looking at the below 3 commands 

```
CREATE TABLE users (
id INTEGER PRIMARY KEY auto_increment,
status SET('NEW', 'OLD', 'AWESOME')
);
```

```
INSERT INTO users (status) VALUES (5);
```

<img width="260" alt="Screenshot 2021-10-05 at 12 06 22 AM" src="https://user-images.githubusercontent.com/69693621/135905756-809a540b-d36e-460c-b05e-314529f815ea.png">

#### Readability of the columns is preserved since MySQL converts the values before displaying automatically

https://user-images.githubusercontent.com/69693621/135990292-1df5bcbe-6752-4726-bc6c-228dab4fce1b.mov

* The above mentioned approach can be used by defining the column in DB as SET type (by defining the same element(s) from the java enum class) 
* More database readability is achieved compared to previous approach, consider a checkbox `Transient ischaemic attack`, we can declare it inside the created Java Enum/MySQL SET and it will be diplayed automatically if selected, by MySQL when a read query is performed. better compared to storing a boolean value inside the column `expereinced_transient_ischaemic_attack` or `experieced_tia` (often the latter name is employed by devs to make the column name short which in turn leads to it being more unreadable)
* Using SET, maximum of 64 distinct elements can be configured (64 checkboxes!! 😅)
* If want to store more than 64 elements (likely won't happen), int datatype will fit the need since the maximum value that can be stored in an int dataype in MySQL is `2147483647`
* Sets can be used to store multiple `checkbox` values, similarly MySQL Enums can be used to handle various `radiobuttons` associated with a question (single value)

--- 
### Native SQL

* Query to find out all the patients who have selected `cough` in symptoms
  * **Using previous approach**
  ```
  SELECT pa.patient_id FROM patients AS pa LEFT JOIN appointments AS app 
  ON pa.patient_id = app.patient_id LEFT JOIN symptoms AS symp ON app.appointment_id 
  = symp.appointment_id WHERE symp.has_cough = 1;
  ```
  * **Using the current approach** (any of the below given 2 can be used, the latter would be recommended since we would have the ordinal of ENUM type to search in Java/Server application)
  ```
  SELECT * FROM appointments WHERE FIND_IN_SET('COUGH',symptoms)>0;
  ```
  ```
  SELECT * FROM appointments WHERE symptoms & 2 = 2;
  ```
---
## Implementation on frontend
#### Tech stack considered: Angular and Typescript

`TODO: Write details`

---
## Project Local Setup and Explanation

#### Local Setup
* Install Java 17 (recommended to use [SdkMan](https://sdkman.io))

`sdk install java 17-open`
* Install Maven (recommended to use [SdkMan](https://sdkman.io))

`sdk install maven`

* Clone the repo and run the below command in core

`mvn clean install`

* Start the application

`mvn spring-boot:run &`

* Access the swagger-ui

`http://localhost:8080/swagger-ui.html`

#### Quick Project implementation
* Only two entities are present in the system
  * patients
  * appointments (many-to-one with patients)
* 5 random patient objects are created on application startup and inserted in H2 in-memory database, the patient-details (id) can be fetched with the /patients GET API (No need to create patient to create appointment, existing patients can be used)

#### important packages/classes
* [containing both entity classes](https://github.com/hardikSinghBehl/checkbox-values-bitfield-enum-poc/tree/main/src/main/java/com/behl/brahma/entity)
* [containing both constant enums](https://github.com/hardikSinghBehl/checkbox-values-bitfield-enum-poc/tree/main/src/main/java/com/behl/brahma/constant)
* [utility class for Set<Enum><->value convertion](https://github.com/hardikSinghBehl/checkbox-values-bitfield-enum-poc/blob/main/src/main/java/com/behl/brahma/utility/BitUtil.java)

---
