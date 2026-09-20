# SmartSupport AI — E-commerce Customer Support

Java Spring Boot + MySQL + OpenAI Responses API customer-support demo. The chatbot covers order tracking, delivery, returns/exchange, payment failures, refunds, coupons, shopping help and general support.

## Requirements
- JDK 17+
- Maven 3.9+ (or add the Maven Wrapper)
- MySQL 8+
- OpenAI API key (optional for AI-generated general replies; verified transactional replies work from MySQL)

## Database
```sql
CREATE DATABASE smartsupport_db;
```
Then set the MySQL password in `src/main/resources/application.properties`.

## AI key
Set the environment variable before running:
```powershell
$env:OPENAI_API_KEY="your_key_here"
```
The application already uses `gpt-5.6-luna` in `application.properties`.

## Run
```bash
mvn spring-boot:run
```
Open http://localhost:8080

## Demo records
The first startup seeds demo records so you can test the complete flow:
- `ORD1001` — shipped/in transit
- `ORD1002` — payment failed + amount debited + refund initiated
- `ORD1003` — delivered + return request
- `ORD1004` — delivered + completed refund
- `WELCOME10` and `FESTIVE20` — active coupons

## Important architecture
Transactional questions are checked against MySQL before the AI answer engine. The AI is never trusted to invent order/payment/refund facts. For general questions, the Responses API receives verified business, FAQ and relevant e-commerce context.

## APIs
- `POST /api/chat`
- `GET /api/faqs`
- `POST /api/faqs`
- `PUT /api/faqs/{id}`
- `DELETE /api/faqs/{id}`
- `GET /api/business`
- `PUT /api/business`
- `GET /api/dashboard`
