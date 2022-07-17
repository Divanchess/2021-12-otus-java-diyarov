insert into addresses (street) values ('Street1');
insert into addresses (street) values ('Street2');
insert into addresses (street) values ('Street3');
insert into addresses (street) values ('Street4');
insert into addresses (street) values ('Street5');

insert into clients (name, address_id) values ('Client1', 1);
insert into clients (name, address_id) values ('Client2', 2);
insert into clients (name, address_id) values ('Client3', 3);
insert into clients (name, address_id) values ('Client4', 4);
insert into clients (name, address_id) values ('Client5', 5);

insert into phones (number, client_id) values ('12345-1', 1);
insert into phones (number, client_id) values ('12345-2', 2);
insert into phones (number, client_id) values ('12345-3', 3);
insert into phones (number, client_id) values ('12345-4', 4);
insert into phones (number, client_id) values ('12345-5', 5);
insert into phones (number, client_id) values ('12345-6', 1);
insert into phones (number, client_id) values ('12345-7', 1);

insert into users (id, name, login, password) values (1, 'UserName1', 'user1', 'pass1');
insert into users (id, name, login, password) values (2, 'UserName2', 'user2', 'pass2');