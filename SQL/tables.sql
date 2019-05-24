SET SERVEROUTPUT ON

create table userDetails (
    name varchar(20) not null,
    username varchar(20) not null primary key,
    password varchar(20) not null
);

create table adminDetails (
    name varchar(20) not null,
    username varchar(20) not null primary key,
    password varchar(20) not null
);
insert all
    into adminDetails values ('naman','n234','12345')
select * from dual;
insert all
    into userDetails values ('naman','naman1','12345')
select * from dual;
insert into adminDetails ('naman','n234','12345');

create table album (
    id varchar(20) primary key,
    name varchar(20) not null
);

create table artist (
    id varchar(20) primary key,
    name varchar(20) not null,
    about varchar(1000)
);

create table song (
    id varchar(20) primary key,
    name varchar(20) not null,
    album_id varchar(20) not null,
    genre varchar(20),
    play_count int not null,
    song_num int,
    wav blob,
    foreign key (album_id) references album(id)
);

create table song_artist (
    song_id varchar(20) not null,
    artist_id varchar(20) not null,
    foreign key (song_id) references song(id),
    foreign key (artist_id) references artist(id),
    primary key(song_id, artist_id)
);

create table album_artist (
    album_id varchar(20) not null,
    artist_id varchar(20) not null,
    foreign key (album_id) references album(id),
    foreign key (artist_id) references artist(id),
    primary key(album_id, artist_id)
);

create table history (
    user_id varchar(20) not null,
    song_id varchar(20) not null,
    last_played date not null,
    foreign key (song_id) references song(id),
    foreign key (user_id) references userDetails(username),
    primary key(user_id, song_id)
);

