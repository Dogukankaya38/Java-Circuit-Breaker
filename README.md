
📘 Resilience4j Demo Project — Spring Boot

Bu proje, Resilience4j CircuitBreaker, Retry, RateLimiter ve Bulkhead bileşenlerini Spring Boot üzerinde kullanarak dayanıklı bir servis mimarisi oluşturmayı gösteren örnek bir uygulamadır.

Proje içerisinde:

    Özel bir ResilientExecutor

    YAML üzerinden konfigüre edilen Resilience4j ayarları

    Circuit Breaker kontrollü ProductService

    Rastgele ürün üreten mock API

    Fallback mekanizması

    GET endpoint ile çalışan ProductController

bulunmaktadır.

📂 Proje Yapısı

    src/main/java
    ├── controller
    │     └── ProductController.java
    ├── service
    │     └── ProductService.java
    ├── resilience
    │     ├── ResilientExecutor.java
    │     ├── SupplierDecorator.java
    │     └── RunnableDecorator.java
    ├── model
    │     └── Product.java

⚙ Resilience4j Parametreleri Neye Yarar?

    resilience4j.circuitbreaker:
    instances:
            default:
            failure-rate-threshold: 50
            slow-call-rate-threshold: 80
            slow-call-duration-threshold: 2s
            minimum-number-of-calls: 5
            sliding-window-size: 10
            wait-duration-in-open-state: 5s
            permitted-number-of-calls-in-half-open-state: 3

🔵 Failure-rate-threshold

    Circuit Breaker’ın açılmasına sebep olan hata oranı eşiği.

    Örneğin:
    failure-rate-threshold: 50
    → Çağrıların %50’si hata verirse Circuit Breaker OPEN olur.

🟡 Slow-call-rate-threshold

    Yavaş çağrı oranı eşiği.

    slow-call-rate-threshold: 80
    → Çağrıların %80’i “slow” olarak işaretlenirse CB açılır.

🟠 Slow-call-duration-threshold

    Bir çağrının “yavaş” sayılması için geçmesi gereken süre.

    slow-call-duration-threshold: 2s
    → 2 saniyeden uzun süren çağrılar slow call sayılır.

🟣 Minimum-number-of-calls

    Circuit Breaker’ın devreye girmesi için gereken minimum çağrı sayısı.

    minimum-number-of-calls: 5
    → En az 5 çağrıdan sonra istatistik değerlendirmesi başlar.

🟤 Sliding-window-size

    Değerlendirilecek çağrı sayısı (istatistik penceresi boyutu).

    sliding-window-size: 10
    → Son 10 çağrı üzerinden hata oranı hesaplanır.

🔴 Wait-duration-in-open-state

    Circuit Breaker “OPEN” olduktan sonra bekleme süresi.

    wait-duration-in-open-state: 5s
    → 5 saniye boyunca tüm çağrılar fallback’e gider.

🟢 Permitted-number-of-calls-in-half-open-state

    Circuit Breaker “HALF-OPEN” modunda kaç test çağrısına izin verileceği.

    permitted-number-of-calls-in-half-open-state: 3
    → 3 çağrı başarılı olursa CB tekrar CLOSED olur.

⚠️ ÖNEMLİ NOT

    Bu projede kullanılan Circuit Breaker, Retry, RateLimiter ve Bulkhead ayarları sadece demo amaçlıdır.

    🔸 Gerçek projelerde bu konfigürasyonlar servis özelinde belirlenmelidir.
    Her servisin:

    trafik yoğunluğu

    SLA gereksinimleri

    ortalama response time

    hata toleransı

    network gecikmesi

    third-party API davranışları

    gibi faktörler dikkate alınarak kendi özel CB/Retry/RateLimiter ayarları yapılmalıdır.

    Hiçbir proje diğerini birebir taşıyamaz — bu ayarlar duruma göre optimize edilmelidir.
