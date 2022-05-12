import { Component } from '@angular/core';
import { Authority } from './generated/models/authority';
import { UserDto } from './generated/models/user-dto';
import { EnumPlaytestControllerService, MapPlaytestControllerService, UserControllerService } from './generated/services';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  private readonly NB_CHARACTERS = 150;

  title = 'sample-frontend';

  result: string[] = ["Awaiting webservice request"];

  compteur: number = 0;

  constructor(private readonly userControllerService: UserControllerService, 
    private readonly enumPlaytestControllerService: EnumPlaytestControllerService,
    private readonly mapPlaytestControllerService: MapPlaytestControllerService) {

  }

  public async callWebservice() {

    switch(this.compteur) {
      case 0: {
        this.displayResponse(await this.enumPlaytestControllerService.getAuthorityWrapper());
        break; 
      } 
      case 1: {   
        this.displayResponse(await this.enumPlaytestControllerService.getAuthorities());
        break; 
      }
      case 2: {   
        this.displayResponse(await this.enumPlaytestControllerService.getAuthority());
        break; 
      }
      case 3: {   
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityWrapper(
          {
            body: {
              authority: 'ACCESS_APP',
              authorityList: ['READ_USER', 'UPDATE_USER']
            }
          }));
        break; 
      }
      case 4: {   
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityList({body: ['READ_USER', 'UPDATE_USER']}));
        break; 
      }
      case 5: {  
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityAsPathParam({authority: 'UPDATE_USER'})); 
        break; 
      }
      case 6: { 
        this.displayResponse(await this.enumPlaytestControllerService.setAuthorityAsQueryParam({authority: 'READ_USER'}));  
        break; 
      }
      case 7: {   
        this.displayResponse(await this.userControllerService.updateUser({body: this.createUser()}));  
        break; 
      }
      case 8: {   
        this.displayResponse(await this.userControllerService.getAllUsernames());  
        break; 
      }
      case 9: {   
        this.displayResponse(await this.userControllerService.getNbUsers()); 
        break; 
      }
      case 10: {   
        this.displayResponse(await this.userControllerService.getNumberList()); 
        break; 
      }
      case 11: {   
        this.displayResponse(await this.mapPlaytestControllerService.getMapString()); 
        break; 
      }
      case 12: { 
        this.displayResponse(await this.mapPlaytestControllerService.getMapUsers());   
        break; 
      }
      case 13: {   
        this.displayResponse(await this.mapPlaytestControllerService.postMapInt({
          body: {
            22:'Okidoki',
            23: 'alright'
          }
        }));   
        break; 
      }
      case 14: {   
        this.displayResponse(await this.mapPlaytestControllerService.postMapAccount({
          body: {
            '22': this.createUser(),
            '23': this.createUser()
          }
        })); 
        break; 
      }
      case 15: {   
        break; 
      }
    }
    this.compteur++;
  }

  public async sendFiles(eventTarget: EventTarget | null) {
    if(eventTarget) {
      const fileList: FileList | null = (eventTarget as HTMLInputElement).files;
      if(fileList && fileList.length > 0) {
        const files: File[] = [];
        for(let i = 0; i < fileList.length; i++) {
          const file = fileList.item(i);
          if(file !== null) {
            files.push(file);
            
          } 
        }
        this.displayResponse(await this.userControllerService.uploadFiles({body: {
          files
        }})); 
      }
     
    }
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
      let firstIndex = 0;
      for (let i = 0; i < nbSlice; i++) {
        this.result[i] = r.slice(firstIndex, firstIndex + this.NB_CHARACTERS);
        firstIndex += this.NB_CHARACTERS;
      }
      if(firstIndex < r.length - 1) {
        this.result[nbSlice] = r.slice(firstIndex);
      }
    }
   
  }
}
