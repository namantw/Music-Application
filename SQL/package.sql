CREATE OR REPLACE PROCEDURE ADDALBUM (
        in_id IN album.id%type,
        in_name IN album.name%type)
    IS 
    BEGIN 
        INSERT INTO album (id,name) 
         VALUES(in_id, in_name); 
        dbms_output.put_line('Added '||in_name||' to Album!'); 
    EXCEPTION
        WHEN others THEN 
            dbms_output.put_line('Error!'); 
END ADDALBUM; 
/
    
CREATE OR REPLACE PROCEDURE ADDARTIST(
        in_id IN artist.id%type,
        in_name IN artist.name%type,
        in_about IN artist.about%type)
    IS 
    BEGIN 
        INSERT INTO artist (id,name,about) 
         VALUES(in_id, in_name, in_about); 
        dbms_output.put_line('Added '||in_name||' to Artist!'); 
    EXCEPTION
        WHEN others THEN 
            dbms_output.put_line('Error!'); 
END ADDARTIST;
/


CREATE OR REPLACE PROCEDURE ADDSONGARTIST (
        in_song_id IN song_artist.song_id%type,
        in_artist_id IN song_artist.artist_id%type)
    IS 
    BEGIN 
        INSERT INTO song_artist (song_id, artist_id) 
         VALUES(in_song_id, in_artist_id); 
    EXCEPTION
        WHEN others THEN 
            dbms_output.put_line('Error!'); 
END ADDSONGARTIST; 
/

CREATE OR REPLACE PROCEDURE ADDSONGTOHISTORY (
    in_user_id IN userDetails.username%type,
    in_song_id IN song_artist.song_id%type)
    IS
    BEGIN
        INSERT INTO history (user_id, song_id, last_played) 
         VALUES(in_user_id, in_song_id, SYSDATE); 
    EXCEPTION
        WHEN others THEN 
            dbms_output.put_line('Error!'); 
END ADDSONGTOHISTORY; 
/

CREATE OR REPLACE TRIGGER DELETESONGINFO BEFORE
    DELETE ON song
    FOR EACH ROW
BEGIN
    DELETE FROM song_artist WHERE song_id=:OLD.id;
    DELETE FROM history WHERE song_id=:OLD.id;
END;
/    

CREATE SEQUENCE song_sequence;
CREATE SEQUENCE album_sequence;
CREATE SEQUENCE artist_sequence;

CREATE OR REPLACE TRIGGER song_on_insert
    BEFORE INSERT ON SONG
    FOR EACH ROW
BEGIN
    SELECT song_sequence.nextval
    INTO :new.id
    FROM dual;
END;
/

CREATE OR REPLACE TRIGGER album_on_insert
    BEFORE INSERT ON ALBUM
    FOR EACH ROW
BEGIN
    SELECT album_sequence.nextval
    INTO :new.id
    FROM dual;
END;
/

CREATE OR REPLACE TRIGGER artist_on_insert
    BEFORE INSERT ON ARTIST
    FOR EACH ROW
BEGIN
    SELECT artist_sequence.nextval
    INTO :new.id
    FROM dual;
END;
/
