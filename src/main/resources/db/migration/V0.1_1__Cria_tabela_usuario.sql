-- Este é um arquivo que será executado quando a aplicação for iniciar; ele será automaticamente detectado pelo Flyway,
-- o que nos permite criar uma estrutura básica para o banco sem ter que fazê-lo manualmente, deixando o banco da aplicação
-- pronto e de acordo com nosso código Java. É importante que este arquivo NÃO seja modificado após aplicado; caso isso
-- seja necessário, o banco deve ser descartado antes, ou as modificações feitas nele serem desfeitas manualmente.
create table user (
  id int not null primary key auto_increment, -- O ID será definido automaticamente caso não o especifiquemos
  name varchar(100) not null,
  username varchar(50) not null,
  email varchar(100) default null
);
