import { Component } from '@angular/core';
import { Authority, UserDto } from './generated/models';
import { EnumPlaytestControllerService, UserControllerService } from './generated/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private readonly NB_CHARACTERS = 150;

  title = 'sample-frontend';

  result: string[] = ["Awaiting webservice request"];

  constructor(private readonly userControllerService: UserControllerService, 
    private readonly enumPlaytestControllerService: EnumPlaytestControllerService) {

  }

  public async callWebservice() {
    this.displayResponse(await this.enumPlaytestControllerService.setAuthorityAsQueryParam({authority: Authority.UpdateUser}));
  }

  private createUser(): UserDto {
    const user: UserDto = {
      email: 'john@bob.com'
    }
    return user;
  }

  private displayResponse(response: any) {
    this.result = [];
    if(response) {
      const r = JSON.stringify(response);
      const nbSlice = r.length / this.NB_CHARACTERS;
      console.info('nbSlices : '+nbSlice);
      console.info('r.length : '+r.length);
      let firstIndex = 0;
      for (let i = 0; i < nbSlice; i++) {
        this.result[i] = r.slice(firstIndex, firstIndex + this.NB_CHARACTERS);
        console.info('test : ');
        console.info(this.result[i] );
        firstIndex += this.NB_CHARACTERS;
        console.info(firstIndex);
      }
      if(firstIndex < r.length - 1) {
        this.result[nbSlice] = r.slice(firstIndex);
      }
    }
   
  }
}
