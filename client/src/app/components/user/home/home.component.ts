import {Component, OnInit} from '@angular/core';
import {Course} from '../../../models/course';
import {User} from '../../../models/user';
import {UserService} from '../../../services/user.service';
import {Router} from '@angular/router';
import {CourseStudent} from '../../../models/coursestudent';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  courseList: Array<Course>;
  errorMessage: string;
  infoMessage: string;
  currentUser: User;

  constructor(private userService: UserService, private router: Router) {
    this.currentUser = this.userService.currentUserValue;
  }

  ngOnInit() {
    this.findAllCourses();
  }

  findAllCourses() {
    this.userService.findAllCourses().subscribe(data => {
      this.courseList = data;
    });
  }

  enroll(course: Course) {
    if (!this.currentUser) {
      this.errorMessage = 'You should sign in to enroll a course';
      return;
    }
    var courseStudent = new CourseStudent();
    courseStudent.student = this.currentUser;
    courseStudent.course = course;

    this.userService.enroll(courseStudent).subscribe(data => {
      this.infoMessage = 'Mission is completed.';
    }, err => {
      this.errorMessage = 'Unexpected error occured.';
    });
  }

}
