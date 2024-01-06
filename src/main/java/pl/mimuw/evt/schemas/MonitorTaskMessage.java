/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package pl.mimuw.evt.schemas;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

/** Monitor request message sent to the worker service. */
@org.apache.avro.specific.AvroGenerated
public class MonitorTaskMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2904228509138487083L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"MonitorTaskMessage\",\"namespace\":\"pl.mimuw.evt.schemas\",\"doc\":\"Monitor request message sent to the worker service.\",\"fields\":[{\"name\":\"jobId\",\"type\":\"string\"},{\"name\":\"serviceUrl\",\"type\":{\"type\":\"string\",\"logicalType\":\"url\"}},{\"name\":\"pollFrequencySecs\",\"type\":\"int\"},{\"name\":\"taskDeadlineTimestampSecs\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<MonitorTaskMessage> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<MonitorTaskMessage> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<MonitorTaskMessage> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<MonitorTaskMessage> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<MonitorTaskMessage> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this MonitorTaskMessage to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a MonitorTaskMessage from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a MonitorTaskMessage instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static MonitorTaskMessage fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence jobId;
  private java.lang.CharSequence serviceUrl;
  private int pollFrequencySecs;
  private int taskDeadlineTimestampSecs;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public MonitorTaskMessage() {}

  /**
   * All-args constructor.
   * @param jobId The new value for jobId
   * @param serviceUrl The new value for serviceUrl
   * @param pollFrequencySecs The new value for pollFrequencySecs
   * @param taskDeadlineTimestampSecs The new value for taskDeadlineTimestampSecs
   */
  public MonitorTaskMessage(java.lang.CharSequence jobId, java.lang.CharSequence serviceUrl, java.lang.Integer pollFrequencySecs, java.lang.Integer taskDeadlineTimestampSecs) {
    this.jobId = jobId;
    this.serviceUrl = serviceUrl;
    this.pollFrequencySecs = pollFrequencySecs;
    this.taskDeadlineTimestampSecs = taskDeadlineTimestampSecs;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return jobId;
    case 1: return serviceUrl;
    case 2: return pollFrequencySecs;
    case 3: return taskDeadlineTimestampSecs;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: jobId = (java.lang.CharSequence)value$; break;
    case 1: serviceUrl = (java.lang.CharSequence)value$; break;
    case 2: pollFrequencySecs = (java.lang.Integer)value$; break;
    case 3: taskDeadlineTimestampSecs = (java.lang.Integer)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'jobId' field.
   * @return The value of the 'jobId' field.
   */
  public java.lang.CharSequence getJobId() {
    return jobId;
  }


  /**
   * Sets the value of the 'jobId' field.
   * @param value the value to set.
   */
  public void setJobId(java.lang.CharSequence value) {
    this.jobId = value;
  }

  /**
   * Gets the value of the 'serviceUrl' field.
   * @return The value of the 'serviceUrl' field.
   */
  public java.lang.CharSequence getServiceUrl() {
    return serviceUrl;
  }


  /**
   * Sets the value of the 'serviceUrl' field.
   * @param value the value to set.
   */
  public void setServiceUrl(java.lang.CharSequence value) {
    this.serviceUrl = value;
  }

  /**
   * Gets the value of the 'pollFrequencySecs' field.
   * @return The value of the 'pollFrequencySecs' field.
   */
  public int getPollFrequencySecs() {
    return pollFrequencySecs;
  }


  /**
   * Sets the value of the 'pollFrequencySecs' field.
   * @param value the value to set.
   */
  public void setPollFrequencySecs(int value) {
    this.pollFrequencySecs = value;
  }

  /**
   * Gets the value of the 'taskDeadlineTimestampSecs' field.
   * @return The value of the 'taskDeadlineTimestampSecs' field.
   */
  public int getTaskDeadlineTimestampSecs() {
    return taskDeadlineTimestampSecs;
  }


  /**
   * Sets the value of the 'taskDeadlineTimestampSecs' field.
   * @param value the value to set.
   */
  public void setTaskDeadlineTimestampSecs(int value) {
    this.taskDeadlineTimestampSecs = value;
  }

  /**
   * Creates a new MonitorTaskMessage RecordBuilder.
   * @return A new MonitorTaskMessage RecordBuilder
   */
  public static pl.mimuw.evt.schemas.MonitorTaskMessage.Builder newBuilder() {
    return new pl.mimuw.evt.schemas.MonitorTaskMessage.Builder();
  }

  /**
   * Creates a new MonitorTaskMessage RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new MonitorTaskMessage RecordBuilder
   */
  public static pl.mimuw.evt.schemas.MonitorTaskMessage.Builder newBuilder(pl.mimuw.evt.schemas.MonitorTaskMessage.Builder other) {
    if (other == null) {
      return new pl.mimuw.evt.schemas.MonitorTaskMessage.Builder();
    } else {
      return new pl.mimuw.evt.schemas.MonitorTaskMessage.Builder(other);
    }
  }

  /**
   * Creates a new MonitorTaskMessage RecordBuilder by copying an existing MonitorTaskMessage instance.
   * @param other The existing instance to copy.
   * @return A new MonitorTaskMessage RecordBuilder
   */
  public static pl.mimuw.evt.schemas.MonitorTaskMessage.Builder newBuilder(pl.mimuw.evt.schemas.MonitorTaskMessage other) {
    if (other == null) {
      return new pl.mimuw.evt.schemas.MonitorTaskMessage.Builder();
    } else {
      return new pl.mimuw.evt.schemas.MonitorTaskMessage.Builder(other);
    }
  }

  /**
   * RecordBuilder for MonitorTaskMessage instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<MonitorTaskMessage>
    implements org.apache.avro.data.RecordBuilder<MonitorTaskMessage> {

    private java.lang.CharSequence jobId;
    private java.lang.CharSequence serviceUrl;
    private int pollFrequencySecs;
    private int taskDeadlineTimestampSecs;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(pl.mimuw.evt.schemas.MonitorTaskMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.jobId)) {
        this.jobId = data().deepCopy(fields()[0].schema(), other.jobId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.serviceUrl)) {
        this.serviceUrl = data().deepCopy(fields()[1].schema(), other.serviceUrl);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.pollFrequencySecs)) {
        this.pollFrequencySecs = data().deepCopy(fields()[2].schema(), other.pollFrequencySecs);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.taskDeadlineTimestampSecs)) {
        this.taskDeadlineTimestampSecs = data().deepCopy(fields()[3].schema(), other.taskDeadlineTimestampSecs);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
    }

    /**
     * Creates a Builder by copying an existing MonitorTaskMessage instance
     * @param other The existing instance to copy.
     */
    private Builder(pl.mimuw.evt.schemas.MonitorTaskMessage other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.jobId)) {
        this.jobId = data().deepCopy(fields()[0].schema(), other.jobId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.serviceUrl)) {
        this.serviceUrl = data().deepCopy(fields()[1].schema(), other.serviceUrl);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.pollFrequencySecs)) {
        this.pollFrequencySecs = data().deepCopy(fields()[2].schema(), other.pollFrequencySecs);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.taskDeadlineTimestampSecs)) {
        this.taskDeadlineTimestampSecs = data().deepCopy(fields()[3].schema(), other.taskDeadlineTimestampSecs);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'jobId' field.
      * @return The value.
      */
    public java.lang.CharSequence getJobId() {
      return jobId;
    }


    /**
      * Sets the value of the 'jobId' field.
      * @param value The value of 'jobId'.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder setJobId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.jobId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'jobId' field has been set.
      * @return True if the 'jobId' field has been set, false otherwise.
      */
    public boolean hasJobId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'jobId' field.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder clearJobId() {
      jobId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'serviceUrl' field.
      * @return The value.
      */
    public java.lang.CharSequence getServiceUrl() {
      return serviceUrl;
    }


    /**
      * Sets the value of the 'serviceUrl' field.
      * @param value The value of 'serviceUrl'.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder setServiceUrl(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.serviceUrl = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'serviceUrl' field has been set.
      * @return True if the 'serviceUrl' field has been set, false otherwise.
      */
    public boolean hasServiceUrl() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'serviceUrl' field.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder clearServiceUrl() {
      serviceUrl = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'pollFrequencySecs' field.
      * @return The value.
      */
    public int getPollFrequencySecs() {
      return pollFrequencySecs;
    }


    /**
      * Sets the value of the 'pollFrequencySecs' field.
      * @param value The value of 'pollFrequencySecs'.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder setPollFrequencySecs(int value) {
      validate(fields()[2], value);
      this.pollFrequencySecs = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'pollFrequencySecs' field has been set.
      * @return True if the 'pollFrequencySecs' field has been set, false otherwise.
      */
    public boolean hasPollFrequencySecs() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'pollFrequencySecs' field.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder clearPollFrequencySecs() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'taskDeadlineTimestampSecs' field.
      * @return The value.
      */
    public int getTaskDeadlineTimestampSecs() {
      return taskDeadlineTimestampSecs;
    }


    /**
      * Sets the value of the 'taskDeadlineTimestampSecs' field.
      * @param value The value of 'taskDeadlineTimestampSecs'.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder setTaskDeadlineTimestampSecs(int value) {
      validate(fields()[3], value);
      this.taskDeadlineTimestampSecs = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'taskDeadlineTimestampSecs' field has been set.
      * @return True if the 'taskDeadlineTimestampSecs' field has been set, false otherwise.
      */
    public boolean hasTaskDeadlineTimestampSecs() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'taskDeadlineTimestampSecs' field.
      * @return This builder.
      */
    public pl.mimuw.evt.schemas.MonitorTaskMessage.Builder clearTaskDeadlineTimestampSecs() {
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public MonitorTaskMessage build() {
      try {
        MonitorTaskMessage record = new MonitorTaskMessage();
        record.jobId = fieldSetFlags()[0] ? this.jobId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.serviceUrl = fieldSetFlags()[1] ? this.serviceUrl : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.pollFrequencySecs = fieldSetFlags()[2] ? this.pollFrequencySecs : (java.lang.Integer) defaultValue(fields()[2]);
        record.taskDeadlineTimestampSecs = fieldSetFlags()[3] ? this.taskDeadlineTimestampSecs : (java.lang.Integer) defaultValue(fields()[3]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<MonitorTaskMessage>
    WRITER$ = (org.apache.avro.io.DatumWriter<MonitorTaskMessage>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<MonitorTaskMessage>
    READER$ = (org.apache.avro.io.DatumReader<MonitorTaskMessage>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.jobId);

    out.writeString(this.serviceUrl);

    out.writeInt(this.pollFrequencySecs);

    out.writeInt(this.taskDeadlineTimestampSecs);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.jobId = in.readString(this.jobId instanceof Utf8 ? (Utf8)this.jobId : null);

      this.serviceUrl = in.readString(this.serviceUrl instanceof Utf8 ? (Utf8)this.serviceUrl : null);

      this.pollFrequencySecs = in.readInt();

      this.taskDeadlineTimestampSecs = in.readInt();

    } else {
      for (int i = 0; i < 4; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.jobId = in.readString(this.jobId instanceof Utf8 ? (Utf8)this.jobId : null);
          break;

        case 1:
          this.serviceUrl = in.readString(this.serviceUrl instanceof Utf8 ? (Utf8)this.serviceUrl : null);
          break;

        case 2:
          this.pollFrequencySecs = in.readInt();
          break;

        case 3:
          this.taskDeadlineTimestampSecs = in.readInt();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










