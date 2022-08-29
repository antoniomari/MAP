create table room
(
    name varchar(50),
    scenarioOnEnterPath varchar(200),
    primary key(name)
);

create table item
(
    name varchar(50),
    state varchar(50),
    canUse bit,
    primary key(name)
);

create table gameCharacter
(
    name varchar(50),
    state varchar(50),
    primary key(name)
);

create table itemLocation
(
    item varchar(50),
    room varchar(50),
    x int,
    y int,
    primary key (item),
    foreign key (item) references item (name),
    foreign key (room) references room (name)
);

create table characterLocation
(
    gamecharacter varchar(50),
    room varchar(50),
    x int,
    y int,
    primary key (gamecharacter),
    foreign key (gamecharacter) references gameCharacter (name),
    foreign key (room) references room (name)
);

create table inventory
(
    item varchar(50),
    index int,
    primary key (item)
);
