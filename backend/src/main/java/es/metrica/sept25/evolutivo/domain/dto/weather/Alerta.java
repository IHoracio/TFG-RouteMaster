package es.metrica.sept25.evolutivo.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Alerta {
	
	@JsonProperty("sender_name")
	private String senderName;
	
	@JsonProperty("event")
	private String event;
	
	@JsonProperty("start")
	private Long start;
	
	@JsonProperty("end")
	private Long end;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("tags")
	private List<String> tags;

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public Long getEnd() {
		return end;
	}

	public void setEnd(Long end) {
		this.end = end;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public String toString() {
		return "Alerta [senderName=" + senderName + ", event=" + event + ", start=" + start 
				+ ", end=" + end + ", description=" + description + ", tags=" + tags + "]";
	}
}

