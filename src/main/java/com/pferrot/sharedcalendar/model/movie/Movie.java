package com.pferrot.sharedcalendar.model.movie;
// Generated 10 oct. 2008 00:01:18 by Hibernate Tools 3.2.0.b9

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.pferrot.security.model.User;
import com.pferrot.sharedcalendar.model.Ownable;
import com.pferrot.sharedcalendar.model.OwnerHistory;
import com.pferrot.sharedcalendar.model.Person;

/**
 * @author Patrice
 *
 */
@Entity
@Table(name = "MOVIES")
public class Movie implements Serializable, Ownable {

	@Id @GeneratedValue
	@Column(name = "ID")
    private Long id;
	
	@Column(name = "TITLE", nullable = false, length = 50)
    private String title;
	
	@Column(name = "DESCRIPTION", nullable = true, length = 255)
    private String description;
	
	@Column(name = "YEAR", nullable = true)
    private Integer year;
	
	@Column(name = "DURATION", nullable = true)
    private Integer duration;
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.movie.MovieCategory.class)
	private Set<MovieCategory> categories = new HashSet<MovieCategory>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.movie.MovieFormat.class)
	private Set<MovieFormat> formats = new HashSet<MovieFormat>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.movie.MovieLanguage.class)
	private Set<MovieLanguage> languages = new HashSet<MovieLanguage>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.movie.MovieSubtitle.class)
	private Set<MovieSubtitle> subtitles = new HashSet<MovieSubtitle>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	private Set<Person> actors = new HashSet<Person>();
	
	@ManyToMany(targetEntity = com.pferrot.sharedcalendar.model.Person.class)
	private Set<Person> directors = new HashSet<Person>();	
	
	@OneToOne(targetEntity = com.pferrot.security.model.User.class)
	@JoinColumn(name = "USER_ID", nullable = false)
	private User owner;
	
	@OneToMany(mappedBy = "ownable", targetEntity = com.pferrot.sharedcalendar.model.OwnerHistory.class)
	private Set<OwnerHistory> ownerHistories = new HashSet<OwnerHistory>();
	
	
    public Movie() {
    	super();
    }
   
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Set<MovieCategory> getCategories() {
		return categories;
	}

	public void setCategories(Set<MovieCategory> categories) {
		this.categories = categories;
	}

	public Set<MovieFormat> getFormats() {
		return formats;
	}

	public void setFormats(Set<MovieFormat> formats) {
		this.formats = formats;
	}

	public Set<MovieLanguage> getLanguages() {
		return languages;
	}

	public void setLanguages(Set<MovieLanguage> languages) {
		this.languages = languages;
	}

	public Set<MovieSubtitle> getSubtitles() {
		return subtitles;
	}

	public void setSubtitles(Set<MovieSubtitle> subtitles) {
		this.subtitles = subtitles;
	}

	public Set<Person> getActors() {
		return actors;
	}

	public void setActors(Set<Person> actors) {
		this.actors = actors;
	}

	public Set<Person> getDirectors() {
		return directors;
	}

	public void setDirectors(Set<Person> directors) {
		this.directors = directors;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public Set<OwnerHistory> getOwnerHistories() {
		return ownerHistories;
	}

	public void setOwnerHistories(Set<OwnerHistory> ownerHistories) {
		this.ownerHistories = ownerHistories;
	}
}


