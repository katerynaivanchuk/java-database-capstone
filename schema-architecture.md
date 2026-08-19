## 1. Architecture Summary

The Smart Clinic application is built on top of Spring Boot, leveraging its auto-configuration and dependency management for rapid backend development. Requests are handled through a hybrid approach: Server-Side Rendering (SSR) via Thymeleaf for the Admin and Doctor dashboards, and RESTful APIs for communication with external or mobile clients.

The application follows a classic layered architecture with a clear separation of concerns. Controllers receive incoming requests and delegate business logic to the service layer, which in turn communicates with the repository layer. For data storage, the system utilizes a polyglot persistence model: MySQL handles structured relational entities (such as patients, doctors, and appointments via JPA), while MongoDB stores flexible, document-based data such as prescriptions.

## 2. Numbered flow of data and control

1. User accesses AdminDashboard or Appointment pages.
2. The action is routed to the appropriate Thymeleaf or REST controller.
3. The controller calls the service layer to handle the request.
4. The service layer executes the business logic and calls the repository layer if needed.
5. The repository layer interacts with the respective database (MySQL or MongoDB).
6. Once data is retrieved from the database, it is mapped into Java domain entities or document models.
7. In MVC flows, models are passed to Thymeleaf templates, where they are rendered as dynamic HTML for the browser. In REST flows, the same models or DTOs are serialized into JSON and sent back to the client via HTTP response.
