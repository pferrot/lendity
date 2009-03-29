package com.pferrot.sharedcalendar.model.movie;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Language;
import com.pferrot.sharedcalendar.model.Ownable;
import com.pferrot.sharedcalendar.model.OwnerHistoryEntry;
import com.pferrot.sharedcalendar.model.WaitListAware;
import com.pferrot.sharedcalendar.model.WaitListEntry;

/**
 * @author Patrice
 *
 */
@Entity
@Table(name = "MOVIE_INSTANCES")
public class MovieInstance implements Serializable, Ownable, WaitListAware {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@ManyToOne(targetEntity = MovieFormat.class)
	@JoinColumn(name = "MOVIE_FORMAT_ID")
	private MovieFormat format;
	
	@ManyToMany(targetEntity = Language.class)
	@JoinTable(name = "MOVIE_INSTANCES_LANGUAGES",
			joinColumns = @JoinColumn(name = "MOVIE_INSTANCE_ID"),
			inverseJoinColumns = @JoinColumn(name = "LANGUAGE_ID"))		
	private Set<Language> languages = new HashSet<Language>();
	
	@ManyToMany(targetEntity = Language.class)
	@JoinTable(name = "MOVIE_INSTANCES_SUBTITLES",
			joinColumns = @JoinColumn(name = "MOVIE_INSTANCE_ID"),
			inverseJoinColumns = @JoinColumn(name = "LANGUAGE_ID"))	
	private Set<Language> subtitles = new HashSet<Language>();
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User owner;
	
	@OneToMany(mappedBy = "ownable", targetEntity = OwnerHistoryEntry.class,
			   cascade = {CascadeType.PERSIST})
	@OrderBy(value = "startDate DESC")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})	
	private List<OwnerHistoryEntry> ownerHistoryEntries = new ArrayList<OwnerHistoryEntry>();
	
	@OneToMany(mappedBy = "waitListAware", targetEntity = WaitListEntry.class,
			   cascade = {CascadeType.PERSIST})
	@OrderBy(value = "requestDate DESC")
	@Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE})
	private List<WaitListEntry> waitListEntries = new ArrayList<WaitListEntry>();
	
	@ManyToOne(targetEntity = Movie.class)
	@JoinColumn(name = "MOVIE_ID")
	private Movie movie;
	
    public MovieInstance() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public MovieFormat getFormat() {
		return format;
	}

	public void setFormat(MovieFormat format) {
		this.format = format;
	}

	public Set<Language> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<Language> languages) {
		this.languages = languages;
	}

	public Set<Language> getSubtitles() {
		return subtitles;
	}

	public void setSubtitles(Set<Language> subtitles) {
		this.subtitles = subtitles;
	}
	
	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<OwnerHistoryEntry> getOwnerHistoryEntries() {
		return ownerHistoryEntries;
	}

	public void setOwnerHistoryEntries(List<OwnerHistoryEntry> ownerHistoryEntries) {
		this.ownerHistoryEntries = ownerHistoryEntries;
	}

	public List<WaitListEntry> getWaitListEntries() {
		return waitListEntries;
	}

	public void setWaitListEntries(List<WaitListEntry> waitListEntries) {
		this.waitListEntries = waitListEntries;
	}

	public Movie getMovie() {
		return movie;
	}

	public void setMovie(Movie movie) {
		this.movie = movie;
	}
}


