CREATE TABLE Message
(
    Id serial PRIMARY KEY,
    Sender int,
    Receiver int,
    Queue int,
    Context int,
    Priority int,
    Created timestamp,
    Message varchar(255)
);

CREATE TABLE Queue
(
    Id serial PRIMARY KEY,
    Created timestamp
); 

CREATE TABLE Client
(
    Id serial PRIMARY KEY,
    Created timestamp
);

----------------
-- create queue
----------------
CREATE OR REPLACE FUNCTION createQueue(created timestamp)
	RETURNS integer AS
$BODY$
declare
	identifier integer;
begin
	INSERT INTO queue("created") VALUES($1) RETURNING Id into identifier;
	return identifier;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- delete queue
----------------
CREATE OR REPLACE FUNCTION deleteQueue(identifier integer)
	RETURNS void AS
$BODY$
declare
begin
	DELETE FROM queue WHERE Id = $1;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-----------------
-- create client
-----------------
CREATE OR REPLACE FUNCTION createClient(created timestamp)
	RETURNS integer AS
$BODY$
declare
	identifier integer;
begin
	INSERT INTO client("created") VALUES($1) RETURNING Id into identifier;
	return identifier;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

-----------------
-- delete client
-----------------
CREATE OR REPLACE FUNCTION deleteClient(identifier integer)
	RETURNS void AS
$BODY$
declare
begin
	DELETE FROM queue WHERE Id = $1;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;