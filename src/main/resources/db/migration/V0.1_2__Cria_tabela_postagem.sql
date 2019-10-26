create table post (
  id int not null primary key auto_increment,
  title varchar(50) not null,
  body text not null,
  user_id int not null,

  constraint post_fk_user foreign key (user_id) references user (id)
);
