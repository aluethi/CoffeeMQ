CREATE TABLE Message
(
    Id serial PRIMARY KEY,
    Sender int,
    Receiver int,
    Queue int,
    Context int,
    Priority int,
    Created timestamp,
    Message text
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
-- get queue
----------------
CREATE OR REPLACE FUNCTION getQueue(identifier integer)
	RETURNS queue AS
$BODY$
declare
    result_record queue;
begin
    IF EXISTS(SELECT * FROM queue WHERE Id = $1) THEN
	    SELECT * FROM queue INTO result_record WHERE Id = $1;
	    return result_record;
	ELSE
	    RAISE 'No entry found with id %.', $1 USING ERRCODE = 'V2001';
	END IF;
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
    IF EXISTS(SELECT * FROM queue WHERE Id = $1) THEN
	    DELETE FROM queue WHERE Id = $1;
	ELSE
	    RAISE 'No entry found with id %.', $1 USING ERRCODE = 'V2001';
	END IF;
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
    ident integer;
begin
    IF EXISTS(SELECT Id FROM client WHERE Id = $1) THEN
	    DELETE FROM client WHERE Id = $1;
	ELSE
	    RAISE 'No entry found with id %.', $1 USING ERRCODE = 'V2001';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $3) THEN
        IF EXISTS(SELECT Id FROM client WHERE Id = $1) THEN
	        INSERT INTO message("sender", "receiver", "queue", "context", "priority", "created", "message") VALUES($1, $2, $3, $4, $5, $6, $7) RETURNING Id into identifier;
            return identifier;
        ELSE
            RAISE 'No entry found with id % in client table.', $1 USING ERRCODE = 'V2002';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;

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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY created ASC LIMIT 1;
            DELETE FROM message WHERE id = result_record.id;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % in message table.', $1 USING ERRCODE = 'V2004';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY priority ASC, created ASC LIMIT 1;
            DELETE FROM message WHERE id = result_record.id;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % in message table.', $1 USING ERRCODE = 'V2004';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1 AND sender = $2) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY created ASC LIMIT 1;
            DELETE FROM message WHERE id = result_record.id;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % and sender id % in message table.', $1, $2 USING ERRCODE = 'V2005';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1 AND sender = $2) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY priority ASC, created ASC LIMIT 1;
            DELETE FROM message WHERE id = result_record.id;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % and sender id % in message table.', $1, $2 USING ERRCODE = 'V2005';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY created ASC LIMIT 1;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % in message table.', $1 USING ERRCODE = 'V2004';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 ORDER BY priority ASC, created ASC LIMIT 1;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % in message table.', $1 USING ERRCODE = 'V2004';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1 AND sender = $2) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY created ASC LIMIT 1;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % and sender id % in message table.', $1, $2 USING ERRCODE = 'V2005';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
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
    IF EXISTS(SELECT Id FROM queue WHERE Id = $1) THEN
        IF EXISTS(SELECT Id FROM message WHERE queue = $1 AND sender = $2) THEN
            SELECT * INTO result_record FROM message WHERE queue = $1 AND sender = $2 ORDER BY priority ASC, created ASC LIMIT 1;
            return result_record;
        ELSE
            RAISE 'No entry found with queue id % and sender id % in message table.', $1, $2 USING ERRCODE = 'V2005';
        END IF;
	ELSE
	    RAISE 'No entry found with id % in queue table.', $1 USING ERRCODE = 'V2003';
	END IF;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- count clients (needed for management console)
----------------

CREATE OR REPLACE FUNCTION getClientCount()
	RETURNS integer AS
$BODY$
declare
    result_count integer;
begin
	SELECT COUNT(id) FROM client INTO result_count;
	return result_count;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- count messages (needed for management console)
----------------

CREATE OR REPLACE FUNCTION getMessageCount()
	RETURNS integer AS
$BODY$
declare
    result_count integer;
begin
	SELECT COUNT(id) FROM message INTO result_count;
	return result_count;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- count queues (needed for management console)
----------------

CREATE OR REPLACE FUNCTION getQueueCount()
	RETURNS integer AS
$BODY$
declare
    result_count integer;
begin
	SELECT COUNT(id) FROM queue INTO result_count;
	return result_count;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- get all queues (needed for management console)
----------------

CREATE OR REPLACE FUNCTION getAllQueues()
	RETURNS SETOF queue AS
$BODY$
declare

begin
	Return QUERY
	SELECT * FROM queue;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- get all messages from a queue (needed for management console)
----------------

CREATE OR REPLACE FUNCTION getAllMessagesFromQueue(q integer)
	RETURNS SETOF message AS
$BODY$
declare

begin
	Return QUERY
	SELECT * FROM message WHERE queue = $1;
end
$BODY$
LANGUAGE plpgsql VOLATILE
COST 100;

----------------
-- indexes
----------------
CREATE INDEX "created-index"
  ON message
  USING btree
  (created);

CREATE INDEX "priority-index"
  ON message
  USING btree
  (priority);

CREATE INDEX "queue-index"
  ON message
  USING btree
  (queue);

CREATE INDEX "receiver-index"
  ON message
  USING btree
  (receiver);

CREATE INDEX "sender-index"
  ON message
  USING btree
  (sender);

CREATE INDEX "client-id-index"
  ON client
  USING btree
  (id);

CREATE INDEX "queue-id-index"
  ON queue
  USING btree
  (id);