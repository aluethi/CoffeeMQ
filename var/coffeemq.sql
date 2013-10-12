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
    Id int PRIMARY KEY,
    Created timestamp
); 

CREATE TABLE Client
(
    Id int PRIMARY KEY,
    Created timestamp
);

----------------
-- create queue
----------------
CREATE OR REPLACE FUNCTION createQueue(id integer, created timestamp)
	RETURNS void AS
$BODY$
declare

begin
	INSERT INTO queue("id", "created") VALUES($1, $2);
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
CREATE OR REPLACE FUNCTION createClient(id integer, created timestamp)
	RETURNS void AS
$BODY$
declare

begin
	INSERT INTO client("id", "created") VALUES($1, $2);
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
	DELETE FROM client WHERE Id = $1;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- enqueue message
----------------
CREATE OR REPLACE FUNCTION enqueueMessage(sender integer, receiver integer, queue integer, context integer, priority integer, created timestamp, message varchar)
	RETURNS integer AS
$BODY$
declare
     identifier integer;
begin
	INSERT INTO message("sender", "receiver", "queue", "context", "priority", "created", "message") VALUES($1, $2, $3, $4, $5, $6, $7) RETURNING Id into identifier;
    return identifier;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- dequeue oldest message
----------------
CREATE OR REPLACE FUNCTION dequeueOldestMessage(q integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY created ASC LIMIT 1;
	DELETE FROM message WHERE id = result_record.id;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- dequeue oldest message with highest priority
----------------
CREATE OR REPLACE FUNCTION dequeueOldestMessageWithHighestPriority(q integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY priority ASC, created ASC LIMIT 1;
	DELETE FROM message WHERE id = result_record.id;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- dequeue oldest message from sender
----------------
CREATE OR REPLACE FUNCTION dequeueOldestMessageFromSender(q integer, s integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY created ASC LIMIT 1;
	DELETE FROM message WHERE id = result_record.id;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- dequeue oldest message from sender with highest priority
----------------
CREATE OR REPLACE FUNCTION dequeueOldestMessageFromSenderWithHighestPriority(q integer, s integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY priority ASC, created ASC LIMIT 1;
	DELETE FROM message WHERE id = result_record.id;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- peek oldest message
----------------
CREATE OR REPLACE FUNCTION peekOldestMessage(q integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY created ASC LIMIT 1;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- peek oldest message with highest priority
----------------
CREATE OR REPLACE FUNCTION peekOldestMessageWithHighestPriority(q integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY priority ASC, created ASC LIMIT 1;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- peek oldest message from sender
----------------
CREATE OR REPLACE FUNCTION peekOldestMessageFromSender(q integer, s integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY created ASC LIMIT 1;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- peek oldest message from sender with highest priority
----------------
CREATE OR REPLACE FUNCTION peekOldestMessageFromSenderWithHighestPriority(q integer, s integer)
    RETURNS message AS
$BODY$
declare
     result_record message;
begin
	SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY priority ASC, created ASC LIMIT 1;
	return result_record;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;