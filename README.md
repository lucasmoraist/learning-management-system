# Learning Management System

## 1. Core Services (O Coração do Negócio)

### **Catalog Service (ou Course Service)**
É o inventário dos cursos. Responsável por gerenciar o conteúdo que será exibido aos alunos.
* **Responsabilidades:** CRUD de cursos, módulos, lições (metadados), categorias e instrutores.
* **Banco de Dados:** PostgreSQL ou MongoDB (útil se a estrutura das lições for muito flexível).

### **Enrollment Service (Matrículas)**
Gerencia o vínculo entre o Aluno e o Curso.
* **Responsabilidades:** Controlar quem tem acesso a quê, data de expiração da matrícula e status (ativo/suspenso).
* **Integração:** Ouve eventos do *Payment Service* para liberar o acesso automaticamente.

### **Content Delivery Service**
Focado na entrega dos arquivos pesados (vídeos, PDFs).
* **Responsabilidades:** Gerar URLs assinadas (S3/Cloudfront), controle de progresso do vídeo e streaming.
* **Dica:** Não armazene vídeos no seu banco; use este serviço para interfacear com um provedor de storage.

---

## 2. Apoio e Engajamento

### **Progression Service**
Rastreia o que o aluno já consumiu.
* **Responsabilidades:** Salvar quais aulas foram concluídas e calcular a porcentagem total do curso.
* **Performance:** Como recebe muitos "hits" (toda vez que um vídeo acaba), pode usar **Redis** para cache rápido antes de persistir.

### **Certification Service**
O gerador de conquistas.
* **Responsabilidades:** Validar se os requisitos de conclusão foram atingidos e gerar o PDF do certificado.
* **Tecnologia:** Pode usar bibliotecas como iText ou JasperReports.

---

## 3. Administrativo e Vendas

### **Payment Service**
A ponte com o mundo financeiro.
* **Responsabilidades:** Integração com gateways (Stripe, Pagar.me), gestão de assinaturas ou vendas únicas e processamento de reembolsos.
* **Evento:** Publica no **RabbitMQ/Kafka** um evento `OrderPaid` assim que o pagamento é confirmado.

### **User/Identity Service**
Gerenciamento de perfis.
* **Responsabilidades:** Cadastro de usuários, perfis (Admin, Instrutor, Aluno) e preferências.
* **Segurança:** Integrado ao **Spring Security** com OAuth2/JWT.

---

## 4. Infraestrutura (O "Cola" do Projeto)

* **API Gateway (Spring Cloud Gateway):** Único ponto de entrada. Faz o roteamento e pode validar o JWT antes de repassar para os serviços.
* **Service Discovery (Eureka):** Para que os serviços se encontrem sem IPs fixos.
* **Config Server:** Centralizar as propriedades (`application.yml`) de todos os microserviços.
