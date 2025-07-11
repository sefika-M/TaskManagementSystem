import { Component } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,MatTableModule,
MatPaginatorModule,
MatSortModule,
MatFormFieldModule,
MatInputModule,
MatSelectModule,
MatButtonModule]
,
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'TmsFrontEnd';
}
